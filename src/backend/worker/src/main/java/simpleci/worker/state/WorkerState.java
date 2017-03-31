package simpleci.worker.state;

import simpleci.shared.message.LogMessage;
import simpleci.worker.executor.Executor;

import java.util.*;

public class WorkerState {
    public final Date startedAt;
    private final Map<Long, Executor> executorMap = new HashMap<>();

    public WorkerState() {
        this.startedAt = new Date();
    }

    public void addJob(long jobId, Executor executor) {
        synchronized (executorMap) {
            executorMap.put(jobId, executor);
        }
    }

    public void removeJob(long jobId) {
        synchronized (executorMap) {
            executorMap.remove(jobId);
        }
    }

    public void stopAndRemoveJob(long jobId) {
        synchronized (executorMap) {
            if(executorMap.containsKey(jobId)) {
                Executor executor = executorMap.get(jobId);
                executor.stop();
                executorMap.remove(jobId);
            }
        }
    }

    public Set<Long> runningJobs() {
        synchronized (executorMap) {
            return executorMap.keySet();
        }
    }
}
