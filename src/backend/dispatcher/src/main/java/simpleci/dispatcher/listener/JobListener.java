package simpleci.dispatcher.listener;

import simpleci.dispatcher.model.entity.Job;
import simpleci.dispatcher.model.entity.JobStatus;
import simpleci.dispatcher.model.worker.state.WorkersState;
import simpleci.shared.message.JobOutputMessage;
import simpleci.shared.message.JobStartedMessage;
import simpleci.shared.message.JobStoppedMessage;
import simpleci.dispatcher.model.repository.UpdaterRepository;

public class JobListener {
    private final WorkersState state;
    private final UpdaterRepository repository;

    public JobListener(WorkersState state, UpdaterRepository repository) {
        this.state = state;
        this.repository = repository;
    }

    public void jobStart(Job job, JobStartedMessage message)
    {
        state.addJob(message.workerId, message.jobId);
        repository.jobStarted(message.jobId, JobStatus.RUNNING, message.startedAt);
    }

    public void jobStop(Job job, JobStoppedMessage message) {
        state.removeJob(message.workerId, message.jobId);
        repository.jobEnded(message.jobId, message.jobStatus, message.stoppedAt);
    }

    public void jobOutput(JobOutputMessage message) {
        repository.jobLog(message.jobId, message.output);
    }
}
