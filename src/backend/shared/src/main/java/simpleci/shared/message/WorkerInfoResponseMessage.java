package simpleci.shared.message;

import com.google.gson.JsonObject;
import simpleci.shared.worker.options.WorkerOptions;

import java.util.Date;
import java.util.Set;

import static simpleci.shared.JsonUtils.*;
import static simpleci.shared.message.WorkerStartedMessage.parseWorkerOptions;

public class WorkerInfoResponseMessage implements LogMessage {
    public final String workerId;
    public final String workerType;
    public final WorkerOptions workerOptions;
    public final Date startedAt;
    public final Set<Long> jobs;

    public WorkerInfoResponseMessage(
            String workerId,
            String workerType,
            WorkerOptions workerOptions,
            Date startedAt,
            Set<Long> jobs) {
        this.workerId = workerId;
        this.workerType = workerType;
        this.workerOptions = workerOptions;
        this.startedAt = startedAt;
        this.jobs = jobs;
    }

    public WorkerInfoResponseMessage(JsonObject message) {
        workerId = message.get("worker_id").getAsString();
        workerType = message.get("worker_type").getAsString();
        startedAt = tsToDate(message.get("started_at").getAsString());
        workerOptions = parseWorkerOptions(workerType, message.get("worker_options").getAsJsonObject());
        jobs = jsonArrayToLongSet(message.get("jobs").getAsJsonArray());
    }

    @Override
    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "worker_info_response");
        message.addProperty("worker_id", workerId);
        message.addProperty("worker_type", workerType);
        message.addProperty("started_at", date(startedAt));
        message.add("worker_options", workerOptions.toJson());
        message.add("jobs", setOfLong(jobs));
        return message;
    }

}
