package simpleci.worker.queue;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.QueueNames;
import simpleci.shared.message.JobStopRequestMessage;
import simpleci.shared.message.WorkerInfoRequestMessage;
import simpleci.shared.message.WorkerInfoResponseMessage;
import simpleci.worker.AppParameters;
import simpleci.worker.state.WorkerState;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ServiceMessageConsumer {
    private final static Logger logger = LoggerFactory.getLogger(ServiceMessageConsumer.class);
    private static final Map<String, Class> MESSAGE_TYPES = new ImmutableMap.Builder<String, Class>()
            .put("job_stop_request", JobStopRequestMessage.class)
            .put("worker_info_request", WorkerInfoRequestMessage.class)
            .put("worker_info_response", WorkerInfoResponseMessage.class)
            .build();

    private final Connection connection;
    private final LogProducer logProducer;
    private final AppParameters parameters;
    private final WorkerState state;


    public ServiceMessageConsumer(
            Connection connection,
            LogProducer logProducer,
            AppParameters parameters,
            WorkerState state) {
        this.connection = connection;
        this.logProducer = logProducer;
        this.parameters = parameters;
        this.state = state;
    }

    public void consume() throws IOException {
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(QueueNames.WORKER_SERVICE_MESSAGES_QUEUE, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, QueueNames.WORKER_SERVICE_MESSAGES_QUEUE, "");

        final JsonParser parser = new JsonParser();
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String messageStr = new String(body, "UTF-8");
                JsonObject message = parser.parse(messageStr).getAsJsonObject();
                processMessage(message);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    private void processMessage(JsonObject message) {
        if (!message.has("type")) {
            logger.error("Type field must exists: " + message.toString());
            return;
        }
        String messageType = message.get("type").getAsString();
        if (!MESSAGE_TYPES.containsKey(messageType)) {
            logger.error("Unknown message type: " + message.toString());
        }
        Class messageClass = MESSAGE_TYPES.get(messageType);
        try {
            Object messageObj = messageClass.getConstructor(JsonObject.class).newInstance(message);
            handleMessage(messageObj);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | ClassCastException | IllegalStateException e) {
            logger.error("Failed to create message", e);
        }
    }

    private void handleMessage(Object messageObj) {
        if (messageObj instanceof JobStopRequestMessage) {
            stopJob(((JobStopRequestMessage) messageObj).jobId);
        } else if (messageObj instanceof WorkerInfoRequestMessage) {
            workerInfo();
        }
    }

    private void workerInfo() {
            WorkerInfoResponseMessage message = new WorkerInfoResponseMessage(
                    parameters.workerId,
                    parameters.workerType,
                    parameters.workerOptions,
                    state.startedAt,
                    state.runningJobs());
            logProducer.send(message);
    }

    private void stopJob(int jobId) {
        logger.info(String.format("Will stop job %d", jobId));
        state.stopAndRemoveJob(jobId);
    }


}
