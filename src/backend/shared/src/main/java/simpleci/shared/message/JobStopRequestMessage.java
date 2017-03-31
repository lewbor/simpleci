package simpleci.shared.message;


import com.google.gson.JsonObject;

public class JobStopRequestMessage implements LogMessage {
    public final int jobId;

    public JobStopRequestMessage(int jobId) {
        this.jobId = jobId;
    }

    public JobStopRequestMessage(JsonObject message) {
        jobId = message.get("job_id").getAsInt();
    }

    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "job_stop_request");
        message.addProperty("job_id", jobId);
        return message;
    }
}
