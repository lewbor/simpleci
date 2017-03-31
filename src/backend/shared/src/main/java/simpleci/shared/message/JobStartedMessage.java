package simpleci.shared.message;

import com.google.gson.JsonObject;
import java.util.Date;

import static simpleci.shared.JsonUtils.tsToDate;
import static simpleci.shared.JsonUtils.date;

public class JobStartedMessage implements LogMessage {
    public final long jobId;
    public final Date startedAt;
    public final String workerId;

    public JobStartedMessage(long jobId, String workerId) {
        this.jobId = jobId;
        this.workerId = workerId;
        this.startedAt = new Date();
    }

    public JobStartedMessage(JsonObject message) {
        jobId = message.get("job_id").getAsInt();
        startedAt = tsToDate(message.get("started_at").getAsString());
        workerId = message.get("worker_id").getAsString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "job_started");
        message.addProperty("job_id", jobId);
        message.addProperty("worker_id", workerId);
        message.addProperty("started_at", date(startedAt));
        return message;
    }
}
