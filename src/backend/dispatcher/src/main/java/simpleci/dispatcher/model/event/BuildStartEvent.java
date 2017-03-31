package simpleci.dispatcher.model.event;

import java.util.Date;

public class BuildStartEvent {
    public final long buildId;
    public final Date startedAt;

    public BuildStartEvent(long buildId, Date startedAt) {
        this.buildId = buildId;
        this.startedAt = startedAt;
    }
}
