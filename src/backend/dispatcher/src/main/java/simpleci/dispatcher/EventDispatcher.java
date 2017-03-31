package simpleci.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.listener.*;
import simpleci.dispatcher.model.entity.Build;
import simpleci.dispatcher.model.entity.Job;
import simpleci.dispatcher.model.event.BuildStartEvent;
import simpleci.dispatcher.model.event.BuildStopEvent;
import simpleci.dispatcher.model.job.JobDto;
import simpleci.dispatcher.model.repository.Repository;
import simpleci.dispatcher.queue.ServiceMessageProducer;
import simpleci.shared.message.*;

import java.util.List;

public class EventDispatcher {
    private final static Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

    private final Repository repository;
    private final JobToBuildLifecycleListener jobToBuildLifecycleListener;
    private final BuildListener buildLifecycleListener;
    private final JobListener jobListener;
    private final CentrifugoListener centrifugoListener;
    private final WorkerListener workerListener;
    private final ProviderListener providerListener;
    private final ServiceMessageProducer serviceMessageProducer;

    public EventDispatcher(
            Repository repository,
            JobToBuildLifecycleListener jobToBuildLifecycleListener,
            BuildListener buildLifecycleListener,
            JobListener dbJobListener,
            CentrifugoListener centrifugoListener,
            WorkerListener workerListener,
            ProviderListener providerListener,
            ServiceMessageProducer serviceMessageProducer) {
        this.repository = repository;
        this.jobToBuildLifecycleListener = jobToBuildLifecycleListener;
        this.buildLifecycleListener = buildLifecycleListener;
        this.jobListener = dbJobListener;
        this.centrifugoListener = centrifugoListener;
        this.workerListener = workerListener;
        this.providerListener = providerListener;
        this.serviceMessageProducer = serviceMessageProducer;

        this.jobToBuildLifecycleListener.setEventDispatcher(this);
    }

    public void onJobStart(JobStartedMessage message) {
        Job job = repository.findJob(message.jobId);
        if(job == null) {
            logger.info("There is no job id={}", message.jobId);
            return;
        }
        jobToBuildLifecycleListener.jobStart(job, message);
        jobListener.jobStart(job, message);
        centrifugoListener.jobStart(job, message);
    }

    public void onJobStop(JobStoppedMessage message) {
        Job job = repository.findJob(message.jobId);
        if(job == null) {
            logger.info("There is no job id={}", message.jobId);
            return;
        }

        jobListener.jobStop(job, message);
        centrifugoListener.jobStop(job, message);
        jobToBuildLifecycleListener.jobStop(job, message);
    }

    public void onJobOutput(JobOutputMessage message) {
        jobListener.jobOutput(message);
        centrifugoListener.jobOutput(message);
    }

   public void onBuildRequest(BuildRequestMessage message) {
        jobToBuildLifecycleListener.buildRequest(message);
    }

    public void onBuildStart(BuildStartEvent event) {
        buildLifecycleListener.buildStart(event);
    }

    public void onBuildStop(BuildStopEvent event) {
        buildLifecycleListener.buildStop(event);
    }

    public void onWorkerStart(WorkerStartedMessage message) {
        workerListener.workerStart(message);
    }

    public void onWorkerStop(WorkerStoppedMessage message) {
        workerListener.workerStop(message);
        providerListener.workerStop(message);
    }

    public void onWorkerInfoResponse(WorkerInfoResponseMessage message) {
        workerListener.workerInfo(message);
    }

    public void afterJobsCreated(Build build, List<JobDto> jobList) {
        providerListener.afterJobsCreated(build, jobList);
    }

    public void onJobStopRequest(JobStopRequestMessage message) {
        serviceMessageProducer.send(message);
    }


}
