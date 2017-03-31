import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.message.WorkerStartedMessage;
import simpleci.shared.message.WorkerStoppedMessage;
import simpleci.shared.utils.TestServicesUtils;
import simpleci.worker.AppParameters;
import simpleci.worker.state.WorkerState;
import simpleci.worker.queue.JobConsumer;
import simpleci.worker.queue.LogProducer;
import simpleci.worker.queue.ServiceMessageConsumer;
import simpleci.shared.DiContainer;
import simpleci.worker.ContainerBuilder;
import simpleci.worker.system.Utils;

import java.io.*;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            AppParameters parameters = AppParameters.fromEnv();
            testServices(parameters);

            DiContainer container = ContainerBuilder.buildContainer(parameters);

            sendWorkerStartMessage(container);
            logger.info(String.format("Started worker %s", parameters.workerId));
            logger.info("Worker parameters: " + parameters.toString());

            container.get("service_messages_consumer", ServiceMessageConsumer.class).consume();

            Thread processorThread = new Thread(container.get("job_consumer", JobConsumer.class));
            processorThread.start();
            processorThread.join();

            sendWorkerStopMessage(container);
        } catch (IOException | TimeoutException | InterruptedException e) {
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
    }

    private static void sendWorkerStartMessage(DiContainer container) {
        LogProducer logProducer = container.get("log_producer", LogProducer.class);
        AppParameters parameters = container.get("parameters", AppParameters.class);

        logProducer.send(new WorkerStartedMessage(
                parameters.workerId,
                parameters.workerType,
                parameters.workerOptions,
                new Date()));
    }



    private static void sendWorkerStopMessage(DiContainer container) {
        LogProducer logProducer = container.get("log_producer", LogProducer.class);
        AppParameters parameters = container.get("parameters", AppParameters.class);
        WorkerState state = container.get("state", WorkerState.class);

        logProducer.send(new WorkerStoppedMessage(
                parameters.workerId,
                parameters.workerType,
                parameters.workerOptions,
                new Date(),
                (int) Utils.timeDiffMilliseconds(new Date(), state.startedAt)));
        try {
            container.get("connection", Connection.class).close();
        } catch (IOException e) {
           fail(e);
        }
        System.exit(0);
    }


    private static void fail(Exception e) {
        logger.error("", e);
        System.exit(1);
    }


}
