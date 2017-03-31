package simpleci.dispatcher.listener;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.model.entity.Build;
import simpleci.dispatcher.model.entity.Job;
import simpleci.dispatcher.model.entity.JobStatus;
import simpleci.dispatcher.model.event.BuildStartEvent;
import simpleci.dispatcher.model.event.BuildStopEvent;
import simpleci.dispatcher.model.job.JobDto;
import simpleci.dispatcher.model.job.JobsConfigParser;
import simpleci.dispatcher.model.repository.UpdaterRepository;
import simpleci.shared.job.config.JobConfigurationException;
import simpleci.shared.message.BuildRequestMessage;
import simpleci.shared.message.JobStartedMessage;
import simpleci.shared.message.JobStoppedMessage;
import simpleci.dispatcher.model.repository.Repository;
import simpleci.dispatcher.EventDispatcher;
import simpleci.shared.JsonUtils;

import java.util.*;

public class JobToBuildLifecycleListener {
    private final static Logger logger = LoggerFactory.getLogger(JobToBuildLifecycleListener.class);
    private final static Set<String> FINISHED_STATES = ImmutableSet.of(JobStatus.FINISHED_SUCCESS, JobStatus.FAILED, JobStatus.STOPPED);

    private final Repository repository;
    private final UpdaterRepository updaterRepository;
    private final JobsConfigParser jobsCreator;
    private EventDispatcher eventDispatcher;

    public JobToBuildLifecycleListener(
            Repository repository,
            UpdaterRepository updaterRepository,
            JobsConfigParser jobsCreator            ) {
        this.repository = repository;
        this.updaterRepository = updaterRepository;
        this.jobsCreator = jobsCreator;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void buildRequest(BuildRequestMessage message) {
        final long buildId = message.buildId;

        Build build = repository.findBuild(buildId);

        JsonObject buildConfigJson = new JsonParser().parse(build.config).getAsJsonObject();
        if(!buildConfigJson.has("stages")) {
            logger.error("Build config does not contains stages section");
            return;
        }
        JsonArray stages = buildConfigJson.get("stages").getAsJsonArray();
        if(stages.size() == 0) {
            logger.error("It must be at least one stage");
            return;
        }

        createBuildJobs(build, buildConfigJson, stages.get(0).getAsString());
    }

    private Map<String, String> createStageTransitions(List<String> stages) {
        Map<String, String> transitions = new HashMap<>();
        if(stages.size() <= 1) {
            return transitions;
        }

        for(int i = 0; i < stages.size() - 1; i++) {
            transitions.put(stages.get(i), stages.get(i + 1));
        }
        return transitions;
    }

    private void createBuildJobs(Build build, JsonObject buildConfigJson, String stage) {
        try {
            List<JobDto> jobs = jobsCreator.createJobs(build, buildConfigJson, stage);
            for (JobDto jobDto : jobs) {
                repository.insertJob(jobDto.job);
                logger.info(String.format("Created job %d for build %d", jobDto.job.id, build.id));
            }

            eventDispatcher.afterJobsCreated(build, jobs);
        } catch(JobConfigurationException e) {
            logger.info("Error in parse build config", e);
            updaterRepository.buildStopped(build.id, JobStatus.FAILED, new Date());
        }
    }

    public void jobStart(Job job, JobStartedMessage message) {
        List<Job> buildJobs = repository.buildJobs(job.build.id);

        if (allJobsArePending(buildJobs)) {
            BuildStartEvent event = new BuildStartEvent(job.build.id, message.startedAt);
            eventDispatcher.onBuildStart(event);
        }
    }

    public void jobStop(Job job, JobStoppedMessage message) {
        List<Job> buildJobs = repository.buildJobs(job.build.id);

        if (allJobsAreFinished(buildJobs)) {
            String buildStatus = buildStatus(buildJobs);
            if (buildStatus.equals(JobStatus.FINISHED_SUCCESS)) {
                createJobsForNextState(job);
            }

            BuildStopEvent event = new BuildStopEvent(job.build.id, message.stoppedAt, buildStatus);
            eventDispatcher.onBuildStop(event);
        }
    }

    private void createJobsForNextState(Job job) {
        JsonObject config = new JsonParser().parse(job.build.config).getAsJsonObject();
        List<String> stages = JsonUtils.jsonArrayToStringList(config.getAsJsonArray("stages"));
        Map<String, String> stageTransitions = createStageTransitions(stages);

        if (stageTransitions.containsKey(job.stage)) {
            String nextStage = stageTransitions.get(job.stage);
            createBuildJobs(job.build, config, nextStage);
        }
    }

    private boolean allJobsAreFinished(List<Job> jobs) {
        for (Job buildJob : jobs) {
            if (!FINISHED_STATES.contains(buildJob.status)) {
                return false;
            }
        }
        return true;
    }

    private boolean allJobsArePending(List<Job> jobs) {
        for (Job buildJob : jobs) {
            if (!buildJob.status.equals("pending")) {
                return false;
            }
        }
        return true;
    }

    private String buildStatus(List<Job> buildJobs) {
        for (Job job : buildJobs) {
            if (!job.status.equals(JobStatus.FINISHED_SUCCESS)) {
                return job.status;
            }
        }
        return JobStatus.FINISHED_SUCCESS;
    }
}
