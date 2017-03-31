package simpleci.dispatcher.model.job;

import com.google.gson.JsonObject;
import simpleci.dispatcher.model.entity.Job;
import simpleci.shared.job.JobConfig;

public class JobDto {
    public final Job job;
    public final JobConfig jobConfig;


    public JobDto(Job job, JobConfig jobConfig) {
        this.job = job;
        this.jobConfig = jobConfig;
    }
}
