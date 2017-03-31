package simpleci.shared.message;


import com.google.gson.JsonObject;

public class JobOutputMessage implements LogMessage {
    public final long jobId;
    public final String output;

    public JobOutputMessage(long jobId, String output) {
        this.jobId = jobId;
        this.output = output;
    }

    public JobOutputMessage(JsonObject message) {
        jobId  = message.get("job_id").getAsInt();
        output = message.get("output").getAsString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "job_output");
        message.addProperty("job_id", jobId);
        message.addProperty("output", output);
        return message;
    }

}
