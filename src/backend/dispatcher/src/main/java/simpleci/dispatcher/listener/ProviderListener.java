package simpleci.dispatcher.listener;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.model.entity.Build;
import simpleci.dispatcher.model.entity.Project;
import simpleci.dispatcher.model.entity.provider.GoogleComputeProvider;
import simpleci.dispatcher.model.entity.provider.LocalProvider;
import simpleci.dispatcher.model.entity.provider.Provider;
import simpleci.dispatcher.model.job.JobDto;
import simpleci.dispatcher.model.job.JobSerializer;
import simpleci.dispatcher.model.worker.state.WorkerStateDescription;
import simpleci.dispatcher.model.worker.state.WorkersState;
import simpleci.shared.job.JobSettings;
import simpleci.shared.message.JobRequestMessage;
import simpleci.shared.worker.options.GceWorkerOptions;
import simpleci.shared.message.WorkerStoppedMessage;
import simpleci.dispatcher.model.repository.Repository;
import simpleci.shared.worker.WorkerType;
import simpleci.dispatcher.queue.JobProducer;
import simpleci.dispatcher.AppParameters;
import simpleci.dispatcher.system.api.GceApi;

import java.util.*;

public class ProviderListener {
    private final static Logger logger = LoggerFactory.getLogger(ProviderListener.class);

    private final AppParameters parameters;
    private final Repository repository;
    private final WorkersState state;
    private final JobProducer jobProducer;

    public ProviderListener(
            AppParameters parameters,
            Repository repository,
            WorkersState state,
            JobProducer jobProducer) {
        this.parameters = parameters;
        this.repository = repository;
        this.state = state;
        this.jobProducer = jobProducer;
    }

    public void workerStop(final WorkerStoppedMessage message) {
        if (message.workerType.equals(WorkerType.GCE)) {
            GceWorkerOptions workerOptions = (GceWorkerOptions) message.workerOptions;
            Provider provider = repository.findProvider(workerOptions.gceProviderId);
            final GceApi gce = new GceApi(parameters);
            Thread stopThread = new Thread(() -> gce.stopAndRemoveInstance((GoogleComputeProvider) provider, message.workerId));
            stopThread.start();
        }
    }

    public void afterJobsCreated(Build build, List<JobDto> jobsList) {
        if (jobsList.isEmpty()) {
            return;
        }

        Project project = build.project;
        if (project.provider instanceof LocalProvider) {
            for (JobDto jobInfo : jobsList) {
                JobSettings jobSettings = JobSerializer.toSettings(jobInfo.job);
                JobRequestMessage requestMessage = new JobRequestMessage(jobInfo.jobConfig, jobSettings, new Date());
                jobProducer.send(requestMessage, routingKey(project.provider));
            }
        } else if (project.provider instanceof GoogleComputeProvider) {
            for (JobDto jobInfo : jobsList) {
                JobRequestMessage requestMessage = new JobRequestMessage(jobInfo.jobConfig, JobSerializer.toSettings(jobInfo.job), new Date());
                jobProducer.send(requestMessage, routingKey(project.provider));
            }
            Set<WorkerStateDescription> freeGceWorkers = state.getWorkers(desc -> !desc.hasJobs()
                    && desc.workerType.equals(WorkerType.GCE)
                    && ((GceWorkerOptions) desc.workerOptions).gceProviderId == project.provider.id );
            int instanceCount = jobsList.size() - freeGceWorkers.size();
            if(instanceCount > 0) {
                createInstances(instanceCount, (GoogleComputeProvider) project.provider);
            }
        }
    }

    private String routingKey(Provider provider) {
        if (provider instanceof LocalProvider) {
            return "local";
        } else if (provider instanceof GoogleComputeProvider) {
            return String.format("gcp.%d", provider.id);
        } else {
            logger.error(String.format("Unknown provider: %s", provider.getClass().getName()));
            return "";
        }
    }

    private void createInstances(int instanceCount, GoogleComputeProvider provider) {
        logger.info(String.format("Will create %d instances on gce", instanceCount));

        final GceApi gceApi = new GceApi(parameters);
        List<Thread> createINstaceThreads = new ArrayList<>();
        for (int i = 0; i < instanceCount; i++) {
            UUID workerId = UUID.randomUUID();
            Thread thread = new Thread(() -> gceApi.createInstance(provider, workerId.toString()));
            createINstaceThreads.add(thread);
        }
        for (Thread thread : createINstaceThreads) {
            thread.start();
        }

    }
}
