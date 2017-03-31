package simpleci.dispatcher.listener;


import simpleci.dispatcher.model.event.BuildStartEvent;
import simpleci.dispatcher.model.event.BuildStopEvent;
import simpleci.dispatcher.model.repository.UpdaterRepository;

public class BuildListener {
    private final UpdaterRepository repository;

    public BuildListener(UpdaterRepository repository) {
        this.repository = repository;
    }

    public void buildStart(BuildStartEvent event)
    {
        repository.buildStarted(event.buildId, event.startedAt);
    }

    public void buildStop(BuildStopEvent event) {
        repository.buildStopped(event.buildId, event.status, event.endedAt);
    }
}
