package simpleci.worker.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.worker.options.GceWorkerOptions;
import simpleci.shared.worker.options.LocalWorkerOptions;
import simpleci.worker.AppParameters;
import simpleci.worker.state.WorkerState;
import simpleci.worker.job.JobProcessor;

import java.io.IOException;
import java.util.Date;

public class JobConsumer implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(JobConsumer.class);

    private final Connection connection;
    private final JobProcessor processor;
    private final AppParameters parameters;
    private final WorkerState state;
    public JobConsumer(
            Connection connection,
            JobProcessor jobProcessor,
            AppParameters parameters, WorkerState state) {
        this.connection = connection;
        this.processor = jobProcessor;
        this.parameters = parameters;
        this.state = state;
    }

    @Override
    public void run() {
        try {
            String queueName = queueName(parameters);
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicQos(1);

            BlockingConsumer consumer = new BlockingConsumer(channel);
            channel.basicConsume(queueName, false, consumer);

            logger.info("Listen on queue {}", queueName);
            while (true) {
                try {
                    BlockingConsumer.Delivery delivery = getDelivery(consumer);

                    if (delivery == null) {
                        logger.info("Consumer receive timeout reached. Worker will be exit now");
                        return;
                    }
                    String message = new String(delivery.getBody());
                    processor.process(message);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (InterruptedException | IOException e) {
                    logger.error("", e);
                    return;
                }
            }
        } catch (IOException e) {
            logger.error("Error while consuming job messages", e);
            return;
        }
    }

    private String queueName(AppParameters parameters) {
        if(parameters.workerOptions instanceof LocalWorkerOptions) {
            return "local";
        }else if(parameters.workerOptions instanceof GceWorkerOptions) {
            return String.format("gcp.%d", ((GceWorkerOptions) parameters.workerOptions).gceProviderId);
        } else {
            logger.error(String.format("Unknown provider: %s", parameters.workerOptions.getClass().getName()));
            return "";
        }
    }

    private BlockingConsumer.Delivery getDelivery(BlockingConsumer consumer) throws InterruptedException {
        if (parameters.exitIfInactive) {
            int secondsDiff = seconds(state.startedAt, new Date());
            int waitTime;
            if (secondsDiff <= parameters.minimumRunningTime) {
                waitTime = parameters.minimumRunningTime - secondsDiff;
            } else {
                int granularityCount = (int) Math.ceil((double) secondsDiff / parameters.timeGranulatity);
                waitTime = parameters.timeGranulatity * granularityCount - secondsDiff;
            }

            logger.info(String.format("Waiting for message %d seconds", waitTime));
            return consumer.nextDelivery(waitTime);

        } else {
            return consumer.nextDelivery();
        }
    }

    private int seconds(Date start, Date end) {
        return (int) ((end.getTime() - start.getTime()) / 1000);
    }

}
