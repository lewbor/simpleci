package simpleci.shared.message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BuildRequestMessage implements LogMessage {
    public final int buildId;

    public BuildRequestMessage(JsonObject message) {
       buildId = message.get("build_id").getAsInt();
    }

    @Override
    public JsonElement toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "build_request");
        message.addProperty("build_id", buildId);
        return message;
    }
}
