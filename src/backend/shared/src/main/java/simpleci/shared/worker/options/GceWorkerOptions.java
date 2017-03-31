package simpleci.shared.worker.options;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonObject;

public class GceWorkerOptions implements WorkerOptions {
    public final long gceProviderId;

    public GceWorkerOptions(long gceProviderId) {
        this.gceProviderId = gceProviderId;
    }

    public GceWorkerOptions(JsonObject message) {
        String type = message.get("type").getAsString();
        if (!type.equals("gce")) {
            throw new RuntimeException("Type must be local");
        }
        this.gceProviderId = message.get("gceProviderId").getAsLong();
    }

    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "gce");
        message.addProperty("gceProviderId", gceProviderId);
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(getClass())
                .add("provider_id", gceProviderId)
                .toString();
    }
}
