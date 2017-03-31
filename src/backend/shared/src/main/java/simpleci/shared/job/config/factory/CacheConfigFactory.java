package simpleci.shared.job.config.factory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import simpleci.shared.job.config.CacheConfig;
import simpleci.shared.job.config.JobConfigurationException;

import java.util.ArrayList;
import java.util.List;

import static simpleci.shared.JsonUtils.jsonToListOfString;

public class CacheConfigFactory {

    public static CacheConfig normalize(JsonObject json) throws JobConfigurationException {
        if (!json.has("cache")) {
            return new CacheConfig(false, false, "", new ArrayList<>());
        }
        return normalizeCache(json.get("cache").getAsJsonObject());
    }

    private static CacheConfig normalizeCache(JsonObject json) throws JobConfigurationException {
        boolean pull = defaultValue(json, "pull", true);
        boolean push = defaultValue(json, "push", true);

        String key = "";
        if(json.has("key")) {
            key = json.get("key").getAsString();
        }

        if (!json.has("directories")) {
            throw new JobConfigurationException("Cache section must have directories config");
        }
        List<String> directories = jsonToListOfString((JsonArray) json.get("directories"));
        return new CacheConfig(pull, push, key, directories);
    }

    public static boolean defaultValue(JsonObject json, String key, boolean defaultValue) {
        if (json.has(key)) {
            return json.get(key).getAsBoolean();
        }
        return defaultValue;
    }
}
