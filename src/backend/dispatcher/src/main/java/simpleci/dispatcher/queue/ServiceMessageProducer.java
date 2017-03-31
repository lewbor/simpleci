package simpleci.dispatcher.queue;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.QueueNames;
import simpleci.shared.message.LogMessage;

import java.io.IOException;

public class ServiceMessageProducer {
    private final Logger logger = LoggerFactory.getLogger(ServiceMessageProducer.class);

    private final Channel channel;
    private final Gson gson = new Gson();

    public ServiceMessageProducer(Connection connection) throws IOException {
        channel = connection.createChannel();
        channel.exchangeDeclare(QueueNames.WORKER_SERVICE_MESSAGES_QUEUE, "fanout");
    }

    public void send(LogMessage message) {
        String sendMessage = gson.toJson(message.toJson());
        logger.info("-> " + sendMessage);
        try {
            channel.basicPublish(QueueNames.WORKER_SERVICE_MESSAGES_QUEUE, "", null, sendMessage.getBytes());
        } catch (IOException e) {
            logger.error("Error publish log message", e);
        }
    }
}
