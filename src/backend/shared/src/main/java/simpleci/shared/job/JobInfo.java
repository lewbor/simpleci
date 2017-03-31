package simpleci.shared.job;

import com.google.gson.JsonObject;

public class JobInfo {
    public final long jobId;
    public final long buildId;
    public final long projectId;
    public final int buildNumber;
    public final int jobNumber;
    public final String stage;

    public JobInfo(
            long jobId,
            long buildId,
            long projectId,
            int buildNumber,
            int jobNumber,
            String stage) {
        this.jobId = jobId;
        this.buildId = buildId;
        this.projectId = projectId;
        this.buildNumber = buildNumber;
        this.jobNumber = jobNumber;
        this.stage = stage;
    }

    public JobInfo(JsonObject json) {
        stage = json.get("stage").getAsString();
        projectId = json.get("project_id").getAsInt();
        buildId = json.get("build_id").getAsInt();
        buildNumber = json.get("build_number").getAsInt();
        jobId = json.get("job_id").getAsInt();
        jobNumber = json.get("job_number").getAsInt();
    }

    public JsonObject toJson(){
        JsonObject message = new JsonObject();
        message.addProperty("stage", stage);
        message.addProperty("project_id", projectId);
        message.addProperty("build_id", buildId);
        message.addProperty("build_number", buildNumber);
        message.addProperty("job_id", jobId);
        message.addProperty("job_number", jobNumber);
        return message;
    }
}
