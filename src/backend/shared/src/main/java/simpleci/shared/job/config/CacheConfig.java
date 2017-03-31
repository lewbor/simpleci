package simpleci.shared.job.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import static simpleci.shared.JsonUtils.jsonToListOfString;
import static simpleci.shared.JsonUtils.listOfString;

public class CacheConfig {
    public final boolean pull;
    public final boolean push;
    public final String key;
    public final List<String> directories;


    public CacheConfig(boolean pull,
                       boolean push,
                       String key,
                       List<String> directories) {
        this.pull = pull;
        this.push = push;
        this.key = key;
        this.directories = directories;
    }

    public CacheConfig(JsonObject json) {
        pull = json.get("pull").getAsBoolean();
        push = json.get("push").getAsBoolean();
        key = json.get("key").getAsString();
        directories = jsonToListOfString(json.get("directories").getAsJsonArray());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("pull", pull);
        json.addProperty("push", push);
        json.addProperty("key", key);
        json.add("directories", listOfString(directories));
        return json;
    }
}
