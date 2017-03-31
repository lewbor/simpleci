package simpleci.dispatcher;

import com.rabbitmq.client.Connection;
import simpleci.dispatcher.listener.*;
import simpleci.dispatcher.model.job.JobsConfigParser;
import simpleci.dispatcher.model.repository.Repository;
import simpleci.dispatcher.model.repository.UpdaterRepository;
import simpleci.dispatcher.model.worker.state.WorkerStateWatchdog;
import simpleci.dispatcher.model.worker.state.WorkersState;
import simpleci.dispatcher.queue.JobProducer;
import simpleci.dispatcher.queue.LogConsumer;
import simpleci.dispatcher.queue.ServiceMessageProducer;
import simpleci.shared.DiContainer;
import simpleci.dispatcher.system.api.CentrifugoApi;
import simpleci.shared.utils.ConnectionUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ContainerBuilder {

    public static DiContainer build(AppParameters parameters) throws IOException, TimeoutException {
        DiContainer container = new DiContainer();

        container.add("config", parameters);

        addServers(container, parameters);


        container.add("repository", new Repository(
                container.get("data_source", DataSource.class)));

        container.add("repository.updater", new UpdaterRepository(
                container.get("data_source", DataSource.class)));

        container.add("job.producer", new JobProducer(
                container.get("connection", Connection.class)));

        container.add("worker.message_producer", new ServiceMessageProducer(
                container.get("connection", Connection.class)));

        container.add("workers_state", new WorkersState());

        container.add("workers_state.watchdog", new WorkerStateWatchdog(
                container.get("workers_state", WorkersState.class),
                container.get("worker.message_producer", ServiceMessageProducer.class)));

        container.add("job.creator", new JobsConfigParser(
                container.get("repository", Repository.class)));

        container.add("listener.job_to_build", new JobToBuildLifecycleListener(
                container.get("repository", Repository.class),
                container.get("repository.updater", UpdaterRepository.class),
                container.get("job.creator", JobsConfigParser.class)));

        container.add("listener.build", new BuildListener(
                container.get("repository.updater", UpdaterRepository.class)));

        container.add("listener.db_job", new JobListener(
                container.get("workers_state", WorkersState.class),
                container.get("repository.updater", UpdaterRepository.class)));

        container.add("listener.centrifugo", new CentrifugoListener(
                container.get("repository", Repository.class),
                container.get("centrifugo_api", CentrifugoApi.class)));

        container.add("listener.provider", new ProviderListener(
                container.get("config", AppParameters.class),
                container.get("repository", Repository.class),
                container.get("workers_state", WorkersState.class),
                container.get("job.producer", JobProducer.class)));

        container.add("listener.worker", new WorkerListener(
                container.get("workers_state", WorkersState.class)));

        container.add("event_dispatcher", new EventDispatcher(
                container.get("repository", Repository.class),
                container.get("listener.job_to_build", JobToBuildLifecycleListener.class),
                container.get("listener.build", BuildListener.class),
                container.get("listener.db_job", JobListener.class),
                container.get("listener.centrifugo", CentrifugoListener.class),
                container.get("listener.worker", WorkerListener.class),
                container.get("listener.provider", ProviderListener.class),
                container.get("worker.message_producer", ServiceMessageProducer.class)));

        container.add("log_consumer", new LogConsumer(
                container.get("connection", Connection.class),
                container.get("event_dispatcher", EventDispatcher.class)));

        return container;
    }

    private static void addServers(DiContainer container, AppParameters parameters) throws IOException, TimeoutException {
        container.add("connection", ConnectionUtils.createRabbitmqConnection(
                parameters.rabbitmqHost,
                parameters.rabbitmqPort,
                parameters.rabbitmqUser,
                parameters.rabbitmqPassword));

        container.add("data_source", ConnectionUtils.createDataSource(
                parameters.databaseHost,
                parameters.databasePort,
                parameters.databaseName,
                parameters.databaseUser,
                parameters.databasePassword));

        container.add("centrifugo_api", new CentrifugoApi(
                parameters.redisHost,
                parameters.redisPort));
    }
}
