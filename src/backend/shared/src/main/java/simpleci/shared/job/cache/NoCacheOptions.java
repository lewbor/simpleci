package simpleci.shared.job.cache;

import com.google.gson.JsonObject;

public class NoCacheOptions extends CacheOptions {
    public NoCacheOptions() {
        super(CacheType.NO_CACHE);
    }

    @Override
    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", type);
        return message;
    }
}
