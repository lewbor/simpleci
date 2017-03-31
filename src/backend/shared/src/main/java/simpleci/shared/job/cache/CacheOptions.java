package simpleci.shared.job.cache;

import com.google.gson.JsonObject;

public abstract class CacheOptions {
    public final String type;

    protected CacheOptions(String type) {
        this.type = type;
    }

    public abstract JsonObject toJson();
}
