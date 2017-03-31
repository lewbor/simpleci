package simpleci.worker.queue;

import com.rabbitmq.client.*;
import com.rabbitmq.utility.Utility;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingConsumer extends DefaultConsumer {
    private final BlockingQueue<Delivery> _queue;


    public BlockingConsumer(Channel ch) {
        super(ch);
        this._queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body)
            throws IOException {
        this._queue.add(new Delivery(envelope, properties, body));
    }

    /**
     * Encapsulates an arbitrary message - simple "bean" holder structure.
     */
    public static class Delivery {
        private final Envelope _envelope;
        private final AMQP.BasicProperties _properties;
        private final byte[] _body;

        public Delivery(Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
            _envelope = envelope;
            _properties = properties;
            _body = body;
        }

        public Envelope getEnvelope() {
            return _envelope;
        }

        public AMQP.BasicProperties getProperties() {
            return _properties;
        }

        public byte[] getBody() {
            return _body;
        }
    }


    public Delivery nextDelivery()
            throws InterruptedException, ShutdownSignalException, ConsumerCancelledException {
        return _queue.take();
    }

    public Delivery nextDelivery(long timeout)
            throws InterruptedException, ShutdownSignalException, ConsumerCancelledException {
        return _queue.poll(timeout, TimeUnit.SECONDS);
    }
}

