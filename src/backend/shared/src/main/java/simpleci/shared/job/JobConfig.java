package simpleci.shared.job;


import com.google.gson.JsonObject;
import simpleci.shared.job.config.CacheConfig;
import simpleci.shared.job.config.EnvironmentVar;
import simpleci.shared.job.config.JobConfigurationException;
import simpleci.shared.job.config.docker.DockerContainer;

import java.util.List;

import static simpleci.shared.JsonUtils.*;

public class JobConfig {
    public final DockerContainer container;
    public final List<EnvironmentVar> environment;
    public final CacheConfig cache;

    public final List<String> beforeScript;
    public final List<String> script;
    public final List<String> afterSuccess;
    public final List<String> afterFailure;
    public final List<String> afterScript;

    public JobConfig(DockerContainer container,
                     List<EnvironmentVar> environment,
                     CacheConfig cache,
                     List<String> beforeScript,
                     List<String> script,
                     List<String> afterSuccess,
                     List<String> afterFailure,
                     List<String> afterScript) throws JobConfigurationException {
        this.container = container;
        this.environment = environment;
        this.cache = cache;
        this.beforeScript = beforeScript;
        this.script = script;
        this.afterSuccess = afterSuccess;
        this.afterFailure = afterFailure;
        this.afterScript = afterScript;
    }

    public JobConfig(JsonObject json) {
        container = new DockerContainer(json.get("container").getAsJsonObject());
        environment = jsonToList(json.get("environment").getAsJsonArray(), jsonElement -> new EnvironmentVar(jsonElement.getAsJsonObject()));
        cache = new CacheConfig(json.get("cache").getAsJsonObject());
        beforeScript = jsonToListOfString(json.get("before_script").getAsJsonArray());
        script = jsonToListOfString(json.get("script").getAsJsonArray());
        afterSuccess = jsonToListOfString(json.get("after_success").getAsJsonArray());
        afterFailure = jsonToListOfString(json.get("after_failure").getAsJsonArray());
        afterScript = jsonToListOfString(json.get("after_script").getAsJsonArray());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("container", container.toJson());
        json.add("environment", listOfObjects(environment, EnvironmentVar::toJson));
        json.add("cache", cache.toJson());
        json.add("before_script", listOfString(beforeScript));
        json.add("script", listOfString(script));
        json.add("after_success", listOfString(afterSuccess));
        json.add("after_failure", listOfString(afterFailure));
        json.add("after_script", listOfString(afterScript));
        return json;
    }





}
