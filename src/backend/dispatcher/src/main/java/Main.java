import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.ContainerBuilder;
import simpleci.dispatcher.model.worker.state.WorkerStateWatchdog;
import simpleci.dispatcher.queue.LogConsumer;
import simpleci.dispatcher.AppParameters;
import simpleci.shared.DiContainer;
import simpleci.shared.utils.TestServicesUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            AppParameters parameters = AppParameters.fromEnv();
            testServices(parameters);

            DiContainer container = ContainerBuilder.build(parameters);

            container.get("workers_state.watchdog", WorkerStateWatchdog.class).start();
            container.get("log_consumer", LogConsumer.class).consume();
        } catch (Exception e) {
            fail(e);
        }
    }

    private static void testServices(AppParameters parameters) {
        if (!TestServicesUtils.testRabbitmq(
                parameters.rabbitmqHost,
                parameters.rabbitmqPort,
                parameters.rabbitmqUser,
                parameters.rabbitmqPassword)) {
            System.exit(1);
        }

        if (!TestServicesUtils.testDatabase(
                parameters.databaseHost,
                parameters.databasePort,
                parameters.databaseName,
                parameters.databaseUser,
                parameters.databasePassword)) {
            System.exit(1);
        }

        if (!TestServicesUtils.testRedis(
                parameters.redisHost,
                parameters.redisPort)) {
            System.exit(1);
        }
    }

    private static void fail(Exception e) {
        logger.error("", e);
        System.exit(1);
    }


}
