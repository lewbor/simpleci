package simpleci.shared.message;

import com.google.gson.JsonObject;
import simpleci.shared.worker.options.WorkerOptions;
import java.util.Date;
import static simpleci.shared.JsonUtils.tsToDate;
import static simpleci.shared.message.WorkerStartedMessage.parseWorkerOptions;
import static simpleci.shared.JsonUtils.date;

public class WorkerStoppedMessage implements LogMessage {
    public final String workerId;
    public final String workerType;
    public final WorkerOptions workerOptions;
    public final Date stoppedAt;
    public final int executedTime;

    public WorkerStoppedMessage(
            String workerId,
            String workerType,
            WorkerOptions workerOptions,
            Date stoppedAt,
            int executedTime) {
        this.workerId = workerId;
        this.workerType = workerType;
        this.workerOptions = workerOptions;
        this.stoppedAt = stoppedAt;
        this.executedTime = executedTime;
    }

    public WorkerStoppedMessage(JsonObject message) {
        workerId = message.get("worker_id").getAsString();
        workerType = message.get("worker_type").getAsString();
        stoppedAt = tsToDate(message.get("stopped_at").getAsString());
        executedTime = message.get("executed_time").getAsInt();
        workerOptions = parseWorkerOptions(workerType, message.get("worker_options").getAsJsonObject());
    }

    @Override
    public JsonObject toJson() {
        JsonObject message = new JsonObject();

        message.addProperty("type", "worker_stopped");
        message.addProperty("worker_id", workerId);
        message.addProperty("worker_type", workerType);
        message.add("worker_options", workerOptions.toJson());
        message.addProperty("stopped_at", date(stoppedAt));
        message.addProperty("executed_time", executedTime);

        return message;
    }


}
