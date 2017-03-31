package simpleci.worker.executor;


import simpleci.worker.job.JobContext;

public interface Executor {
    String execute(JobContext context);
    void stop();
}
