package simpleci.dispatcher.queue;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.message.JobRequestMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JobProducer {
    private final Logger logger = LoggerFactory.getLogger(JobProducer.class);

    private Channel channel;
    private Set<String> queueNamesCache = new HashSet<>();

    public JobProducer(Connection connection) throws IOException {
        this.channel = connection.createChannel();
    }


    public void send(JobRequestMessage requestMessage, String queueName) {
        try {
            if(!queueNamesCache.contains(queueName)) {
                channel.queueDeclare(queueName, true, false, false, null);
                queueNamesCache.add(queueName);
            }
            logger.info(String.format("Produce job to queue %s", queueName));

            String sendMessage = requestMessage.toJson().toString();
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, sendMessage.getBytes());
        } catch (IOException e) {
            logger.error("Error publish job message", e);
        }
    }


}
