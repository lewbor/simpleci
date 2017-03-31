package simpleci.worker.job;


import simpleci.shared.job.JobConfig;
import simpleci.shared.job.JobSettings;

public class JobContext {
    public final JobSettings jobSettings;
    public final JobConfig jobConfig;
    public final JobEnvVars jobEnvVars;

    public final JobOutputProcessor outputProcessor;

    public JobContext(
            JobSettings jobSettings,
            JobConfig jobConfig,
            JobEnvVars jobEnvVars,
            JobOutputProcessor outputProcessor) {
        this.jobSettings = jobSettings;
        this.jobConfig = jobConfig;
        this.jobEnvVars = jobEnvVars;
        this.outputProcessor = outputProcessor;
    }
}
