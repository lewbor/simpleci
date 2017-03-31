package simpleci.worker.job;

import com.google.common.collect.ImmutableMap;
import simpleci.shared.job.JobConfig;
import simpleci.shared.job.JobSettings;
import simpleci.shared.job.config.EnvironmentVar;
import simpleci.worker.bash.Locations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JobEnvVars {
    public List<EnvironmentVar> allEnvironment;

    public JobEnvVars(JobConfig jobConfig, JobSettings jobSettings) {
        allEnvironment = new ArrayList<>(jobConfig.environment.size());
        allEnvironment.addAll(jobConfig.environment);
        allEnvironment.addAll(jobConfig.container.environment);
        addExtraVars(jobSettings);
    }

    private void addExtraVars(JobSettings jobSettings) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>()
                .put("CI", "true")
                .put("CONTINUOUS_INTEGRATION", "true")
                .put("DEBIAN_FRONTEND", "noninteractive")
                .put("BUILD_DIR", Locations.BUILD_DIR)
                .put("JOB_STAGE", jobSettings.info.stage)
                .put("BUILD_ID", String.valueOf(jobSettings.info.buildId))
                .put("BUILD_NUMBER", String.valueOf(jobSettings.info.buildNumber))
                .put("JOB_ID", String.valueOf(jobSettings.info.jobId))
                .put("JOB_NUMBER", String.valueOf(jobSettings.info.jobNumber))
                .put("COMMIT", jobSettings.repositorySettings.commit)
                .put("COMMIT_RANGE", jobSettings.repositorySettings.commitRange)
                .put("BRANCH", jobSettings.repositorySettings.branch)
                .put("REPOSITORY_URL", jobSettings.repositorySettings.repositoryUrl);
        if (jobSettings.repositorySettings.tag != null) {
            builder.put("TAG", jobSettings.repositorySettings.tag);
        }

        for (Map.Entry<String, String> envVarEntry : builder.build().entrySet()) {
            allEnvironment.add(new EnvironmentVar(envVarEntry.getKey(), envVarEntry.getValue()));
        }
    }
}
