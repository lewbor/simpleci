package simpleci.dispatcher.model.worker.state;

import simpleci.dispatcher.queue.ServiceMessageProducer;
import simpleci.shared.message.WorkerInfoRequestMessage;

public class WorkerStateWatchdog {
    private static final long CHECK_INTERVAL = 60 * 1000;

    private final WorkersState state;
    private final ServiceMessageProducer messageProducer;
    private boolean started = false;

    public WorkerStateWatchdog(
            WorkersState state,
            ServiceMessageProducer messageProducer) {
        this.state = state;
        this.messageProducer = messageProducer;
    }

    public void start() {
        if (!started) {
            Thread thread = new Thread(() -> {
                for(;;) {
                    try {
                        state.clear();
                        messageProducer.send(new WorkerInfoRequestMessage());
                        Thread.sleep(CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
            thread.start();
            started = true;
        }
    }
}
