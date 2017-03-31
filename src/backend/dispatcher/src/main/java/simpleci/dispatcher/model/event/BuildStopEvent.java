package simpleci.dispatcher.model.event;

import java.util.Date;

public class BuildStopEvent
{
    public final long buildId;
    public final Date endedAt;
    public final String status;

    public BuildStopEvent(long buildId, Date endedAt, String status) {
        this.buildId = buildId;
        this.endedAt = endedAt;
        this.status = status;
    }
}
