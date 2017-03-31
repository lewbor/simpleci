package simpleci.shared.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import simpleci.shared.job.JobConfig;
import simpleci.shared.job.JobSettings;
import simpleci.shared.job.config.JobConfigurationException;

import java.util.Date;

import static simpleci.shared.JsonUtils.date;
import static simpleci.shared.JsonUtils.tsToDate;

public class JobRequestMessage implements LogMessage {
    public final JobConfig jobConfig;
    public final JobSettings settings;
    public final Date requestedAt;


    public JobRequestMessage(
            JobConfig jobConfig,
            JobSettings settings,
            Date requestedAt) {
        this.jobConfig = jobConfig;
        this.settings = settings;
        this.requestedAt = requestedAt;
    }

    public JobRequestMessage(JsonObject json){
        jobConfig = new JobConfig(json.get("job_config").getAsJsonObject());
        settings = new JobSettings(json.get("job_settings").getAsJsonObject());
        requestedAt = tsToDate(json.get("requested_at").getAsString());
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add("job_config", jobConfig.toJson());
        json.add("job_settings", settings.toJson());
        json.addProperty("requested_at", date(requestedAt));
        return json;
    }
}
