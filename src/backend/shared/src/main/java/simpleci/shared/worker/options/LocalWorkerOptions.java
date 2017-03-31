package simpleci.shared.worker.options;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonObject;

public class LocalWorkerOptions implements WorkerOptions {

    public LocalWorkerOptions() {

    }

    public LocalWorkerOptions(JsonObject message) {
        String type = message.get("type").getAsString();
        if (!type.equals("local")) {
            throw new RuntimeException("Type must be local");
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "local");
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(getClass())
                .toString();
    }
}
