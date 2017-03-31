package simpleci.worker;

import com.rabbitmq.client.Connection;
import com.spotify.docker.client.DefaultDockerClient;
import simpleci.shared.DiContainer;
import simpleci.shared.utils.ConnectionUtils;
import simpleci.worker.state.WorkerState;
import simpleci.worker.executor.docker.DockerSshExecutor;
import simpleci.worker.executor.shell.ShellExecutor;
import simpleci.worker.job.JobProcessor;
import simpleci.worker.queue.JobConsumer;
import simpleci.worker.queue.LogProducer;
import simpleci.worker.queue.ServiceMessageConsumer;
import simpleci.worker.system.DockerContainerManager;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

public class ContainerBuilder {
    public static DiContainer buildContainer(AppParameters parameters) throws IOException, TimeoutException {
        DiContainer container = new DiContainer();

        container.add("parameters", parameters);
        container.add("state", new WorkerState());

        container.add("connection", ConnectionUtils.createRabbitmqConnection(
                parameters.rabbitmqHost,
                parameters.rabbitmqPort,
                parameters.rabbitmqUser,
                parameters.rabbitmqPassword));


        container.add("log_producer", new LogProducer(
                container.get("connection", Connection.class)));

        container.add("docker_client", new DefaultDockerClient(
                URI.create(container.get("parameters", AppParameters.class).dockerEndpoint)));

        container.add("container_manager", new DockerContainerManager(
                container.get("docker_client", DefaultDockerClient.class)));

        container.add("service_messages_consumer", new ServiceMessageConsumer(
                container.get("connection", Connection.class),
                container.get("log_producer", LogProducer.class),
                container.get("parameters", AppParameters.class),
                container.get("state", WorkerState.class)));

        container.add("executor.docker_ssh", new DockerSshExecutor(
                container.get("container_manager", DockerContainerManager.class) ));

        container.add("executor.shell", new ShellExecutor());

        container.add("job_processor", new JobProcessor(
                container.get("parameters", AppParameters.class),
                container.get("state", WorkerState.class),
                container.get("log_producer", LogProducer.class),
                container.get("executor.docker_ssh", DockerSshExecutor.class),
                container.get("executor.shell", ShellExecutor.class)));

        container.add("job_consumer", new JobConsumer(
                container.get("connection", Connection.class),
                container.get("job_processor", JobProcessor.class),
                container.get("parameters", AppParameters.class),
                container.get("state", WorkerState.class)));

        return container;
    }

}
