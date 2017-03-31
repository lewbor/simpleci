package simpleci.dispatcher.model.job;

import com.google.gson.JsonObject;
import simpleci.dispatcher.model.entity.Build;
import simpleci.dispatcher.model.entity.Job;
import simpleci.dispatcher.model.entity.JobStatus;
import simpleci.dispatcher.model.repository.Repository;
import simpleci.shared.job.JobConfig;
import simpleci.shared.job.config.JobConfigurationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobsConfigParser {
    private final Repository repository;

    public JobsConfigParser(Repository repository) {
        this.repository = repository;
    }

    public List<JobDto> createJobs(Build build, JsonObject buildConfigJson, String stage) throws JobConfigurationException {
        List<JobConfig> jobsConfig = new JobsConfigGenerator().generateJobsConfig(build, buildConfigJson, stage);

        List<JobDto> jobs = new ArrayList<>();
        int currentJobNumber = repository.getJobMaxNumber(build.id) + 1;
        for (JobConfig jobConfig : jobsConfig) {
            Job job = new Job();
            job.number = currentJobNumber;
            job.build = build;
            job.createdAt = new Date();
            job.status = JobStatus.PENDING;
            job.stage = stage;
            job.config = jobConfig.toJson().toString();
            jobs.add(new JobDto(job, jobConfig));

            currentJobNumber++;
        }
        return jobs;
    }


}
