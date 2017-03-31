package simpleci.shared.job;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SshKeyOptions {
    public final boolean hasKey;
    public final String publicKey;
    public final String privateKey;

    public SshKeyOptions() {
        hasKey = false;
        publicKey = "";
        privateKey = "";
    }

    public SshKeyOptions(String publicKey, String privateKey) {
        hasKey = true;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public SshKeyOptions(JsonObject jsonObject) {
        hasKey = jsonObject.get("has_key").getAsBoolean();
        if(hasKey) {
            publicKey = jsonObject.get("public_key").getAsString();
            privateKey = jsonObject.get("private_key").getAsString();
        } else {
            publicKey = "";
            privateKey = "";
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("has_key", hasKey);
        json.addProperty("public_key", publicKey);
        json.addProperty("private_key", privateKey);
        return json;
    }
}
