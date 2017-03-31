package simpleci.dispatcher.listener;

import simpleci.shared.message.WorkerInfoResponseMessage;
import simpleci.shared.message.WorkerStartedMessage;
import simpleci.dispatcher.model.worker.state.WorkerStateDescription;
import simpleci.dispatcher.model.worker.state.WorkersState;
import simpleci.shared.message.WorkerStoppedMessage;

import java.util.*;

public class WorkerListener {
   private final WorkersState state;

    public WorkerListener(WorkersState state) {
        this.state = state;
    }


    public void workerStart(WorkerStartedMessage message) {
        WorkerStateDescription stateDescription = new WorkerStateDescription(
                message.workerId,
                message.workerType,
                message.workerOptions,
                message.startedAt);

        state.addWorker(stateDescription);
    }

    public void workerStop(final WorkerStoppedMessage message) {
        state.removeWorker(message.workerId);

    }

    public void workerInfo(WorkerInfoResponseMessage message) {
        WorkerStateDescription stateDescription = new WorkerStateDescription(
                message.workerId,
                message.workerType,
                message.workerOptions,
                message.startedAt);
        for(Long job : message.jobs) {
            stateDescription.addJob(job);
        }

        state.replaceWorker(stateDescription);
    }
}
