package simpleci.shared.message;

import com.google.gson.JsonObject;

import java.util.Date;

import static simpleci.shared.JsonUtils.tsToDate;
import static simpleci.shared.JsonUtils.date;

public class JobStoppedMessage implements LogMessage{
    public final long jobId;
    public final String workerId;
    public final Date stoppedAt;
    public final String jobStatus;

    public JobStoppedMessage(long jobId, String jobStatus, String workerId) {
        this.jobId = jobId;
        this.jobStatus = jobStatus;
        this.workerId = workerId;
        this.stoppedAt = new Date();
    }

    public JobStoppedMessage(JsonObject message) {
        jobId = message.get("job_id").getAsInt();
        stoppedAt = tsToDate(message.get("stopped_at").getAsString());
        jobStatus = message.get("job_status").getAsString();
        workerId = message.get("worker_id").getAsString();
    }

    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "job_stopped");
        message.addProperty("job_id", jobId);
        message.addProperty("job_status", jobStatus);
        message.addProperty("worker_id", workerId);
        message.addProperty("stopped_at", date(stoppedAt));
        return message;
    }
}
