package simpleci.worker.queue;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.QueueNames;
import simpleci.shared.message.LogMessage;
import java.io.IOException;

public class LogProducer {
    private static final Logger logger = LoggerFactory.getLogger(LogProducer.class);
    private final  Gson gson = new Gson();
    private Channel channel;

    public LogProducer(Connection connection) throws IOException {
        this.channel = connection.createChannel();
        channel.queueDeclare(QueueNames.WORKER_MESSAGE_QUEUE, true, false, false, null);
    }

    public void send(LogMessage message) {
        String sendMessage = gson.toJson(message.toJson());
        try {
            channel.basicPublish("", QueueNames.WORKER_MESSAGE_QUEUE, null, sendMessage.getBytes());
        } catch (IOException e) {
            logger.error("Error publish log message", e);
        }
    }

}
