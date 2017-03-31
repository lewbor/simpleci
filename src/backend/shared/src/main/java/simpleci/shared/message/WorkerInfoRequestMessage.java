package simpleci.shared.message;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WorkerInfoRequestMessage implements LogMessage {

    public WorkerInfoRequestMessage() {

    }

    public WorkerInfoRequestMessage(JsonObject json) {

    }

    @Override
    public JsonElement toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", "worker_info_request");
        return message;
    }
}
