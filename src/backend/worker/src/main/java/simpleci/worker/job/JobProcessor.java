package simpleci.worker.job;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.message.JobOutputMessage;
import simpleci.shared.message.JobRequestMessage;
import simpleci.shared.message.JobStartedMessage;
import simpleci.shared.message.JobStoppedMessage;
import simpleci.worker.AppParameters;
import simpleci.worker.state.WorkerState;
import simpleci.worker.executor.docker.DockerSshExecutor;
import simpleci.worker.executor.Executor;
import simpleci.worker.executor.Executors;
import simpleci.worker.executor.shell.ShellExecutor;
import simpleci.worker.queue.LogProducer;

import java.util.Date;


public class JobProcessor {
    private final Logger logger = LoggerFactory.getLogger(JobProcessor.class);

    private final AppParameters parameters;
    private final WorkerState state;
    private LogProducer logProducer;
    private DockerSshExecutor dockerSshExecutor;
    private ShellExecutor shellExecutor;

    public JobProcessor(
            AppParameters parameters,
            WorkerState state,
            LogProducer logProducer,
            DockerSshExecutor dockerSshExecutor,
            ShellExecutor shellExecutor) {
        this.parameters = parameters;
        this.state = state;
        this.logProducer = logProducer;
        this.dockerSshExecutor = dockerSshExecutor;
        this.shellExecutor = shellExecutor;
    }

    public void process(String messageStr) {
        JobRequestMessage jobRequest;
        try {
            JsonObject messageJson = new JsonParser().parse(messageStr).getAsJsonObject();
            jobRequest = new JobRequestMessage(messageJson);
        } catch (RuntimeException e) {
            logger.error("Error parse job request message: " + e.getMessage());
            return;
        }

        logProducer.send(new JobStartedMessage(jobRequest.settings.info.jobId, parameters.workerId));

        JobOutputProcessor outputProcessor = createOutputProcessor(jobRequest.settings.info.jobId);
        outputProcessor.output(String.format("Using worker: %s\n", parameters.workerId));
        outputProcessor.output(String
                .format("Startup for %d seconds\n", (new Date().getTime() - jobRequest.requestedAt.getTime()) / 1000));

        JobEnvVars jobEnvVars = new JobEnvVars(jobRequest.jobConfig, jobRequest.settings);
        JobContext jobContext = new JobContext(jobRequest.settings, jobRequest.jobConfig, jobEnvVars, outputProcessor);
        String status = execute(jobContext);
        logProducer.send(new JobStoppedMessage(jobRequest.settings.info.jobId, status, parameters.workerId));
    }

    private JobOutputProcessor createOutputProcessor(final long jobId) {
        return output -> logProducer.send(new JobOutputMessage(jobId, output));
    }

    private String execute(JobContext context) {
        Executor executor;
        switch (parameters.executor) {
            case Executors.DOCKER_SSH:
                executor = dockerSshExecutor;
                break;
            case Executors.SHELL:
                executor = shellExecutor;
                break;
            default:
                throw new RuntimeException(String.format("Unknown executor type: %s", parameters.executor));
        }

        state.addJob(context.jobSettings.info.jobId, executor);
        String status = executor.execute(context);
        state.removeJob(context.jobSettings.info.jobId);

        return status;
    }


}
