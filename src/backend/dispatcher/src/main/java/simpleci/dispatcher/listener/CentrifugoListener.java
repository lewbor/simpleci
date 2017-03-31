package simpleci.dispatcher.listener;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.model.entity.Job;
import simpleci.shared.message.JobOutputMessage;
import simpleci.shared.message.JobStartedMessage;
import simpleci.shared.message.JobStoppedMessage;
import simpleci.dispatcher.model.repository.Repository;
import simpleci.dispatcher.system.api.CentrifugoApi;

import java.util.Map;

public class CentrifugoListener {
    private final static Logger logger = LoggerFactory.getLogger(CentrifugoListener.class);
    private final Repository repository;
    private final CentrifugoApi api;

    public CentrifugoListener(Repository repository, CentrifugoApi api) {
        this.repository = repository;
        this.api = api;
    }

    public void jobStart(Job job, JobStartedMessage message) {
        Map<String, Object> sendMessage = new ImmutableMap.Builder<String, Object>()
                .put("job_id", job.id)
                .put("build_id", job.build.id)
                .put("project_id", job.build.project.id)
                .put("action", "job_start")
                .put("started_at", message.startedAt.getTime())
                .build();
        api.send(sendMessage, projectChannelName(job.build.project.id));
    }

    public void jobOutput(JobOutputMessage message) {
        api.send(message.output, jobChannelName(message.jobId));
    }

    public void jobStop(Job job, JobStoppedMessage message) {
        Map<String, Object> sendMessage = new ImmutableMap.Builder<String, Object>()
                .put("job_id", job.id)
                .put("build_id", job.build.id)
                .put("project_id", job.build.project.id)
                .put("action", "job_stop")
                .put("ended_at", message.stoppedAt.getTime())
                .put("job_status", message.jobStatus)
                .build();
        api.send(sendMessage, projectChannelName(job.build.project.id));
    }

    private String projectChannelName(long projectId) {
        return "$project." + projectId;
    }

    private String jobChannelName(long jobId) {
        return "$job." + jobId;
    }
}
