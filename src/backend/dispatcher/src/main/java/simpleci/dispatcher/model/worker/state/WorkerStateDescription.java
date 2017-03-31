package simpleci.dispatcher.model.worker.state;

import simpleci.shared.worker.options.WorkerOptions;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WorkerStateDescription {
    public final String workerId;
    public final String workerType;
    public final WorkerOptions workerOptions;

    public final Date startedAt;
    private final Set<Long> jobs = new HashSet<>();

    public WorkerStateDescription(String workerId, String type, WorkerOptions workerOptions, Date startedAt ) {
        this.workerId = workerId;
        this.workerType = type;
        this.workerOptions = workerOptions;
        this.startedAt = startedAt;

    }

    public void addJob(long jobId) {
        jobs.add(jobId);
    }

    public boolean hasJob(long jobId) {
        return jobs.contains(jobId);
    }

    public boolean hasJobs() {
        return !jobs.isEmpty();
    }

    public void removeJob(long jobId) {
        jobs.remove(jobId);
    }
}
