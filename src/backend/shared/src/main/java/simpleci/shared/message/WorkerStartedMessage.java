package simpleci.shared.message;

import com.google.gson.JsonObject;
import simpleci.shared.worker.WorkerType;
import simpleci.shared.worker.options.GceWorkerOptions;
import simpleci.shared.worker.options.LocalWorkerOptions;
import simpleci.shared.worker.options.WorkerOptions;
import java.util.Date;

import static simpleci.shared.JsonUtils.tsToDate;
import static simpleci.shared.JsonUtils.date;

public class WorkerStartedMessage implements LogMessage {
    public final String workerId;
    public final String workerType;
    public final WorkerOptions workerOptions;
    public final Date startedAt;

    public WorkerStartedMessage(
            String workerId,
            String workerType,
            WorkerOptions workerOptions,
            Date startedAt) {
        this.workerId = workerId;
        this.workerType = workerType;
        this.workerOptions = workerOptions;
        this.startedAt = startedAt;
    }

    public WorkerStartedMessage(JsonObject message) {
        workerId = message.get("worker_id").getAsString();
        workerType = message.get("worker_type").getAsString();
        startedAt = tsToDate(message.get("started_at").getAsString());
        workerOptions = parseWorkerOptions(workerType, message.get("worker_options").getAsJsonObject());
    }


    @Override
    public JsonObject toJson() {
        JsonObject message = new JsonObject();

        message.addProperty("type", "worker_started");
        message.addProperty("worker_id", workerId);
        message.addProperty("worker_type", workerType);
        message.add("worker_options", workerOptions.toJson());
        message.addProperty("started_at", date(startedAt));

        return message;
    }

    public static WorkerOptions parseWorkerOptions(String workerType, JsonObject workerOptionsJson) {
        switch (workerType) {
            case WorkerType.GCE:
                return new GceWorkerOptions(workerOptionsJson);
            case WorkerType.LOCAL:
                return new LocalWorkerOptions(workerOptionsJson);
            default:
                throw new RuntimeException("Incorrect worker type");
        }
    }
}
