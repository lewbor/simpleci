package simpleci.shared.job.config.factory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import simpleci.shared.job.JobConfig;
import simpleci.shared.job.config.CacheConfig;
import simpleci.shared.job.config.EnvironmentVar;
import simpleci.shared.job.config.JobConfigurationException;
import simpleci.shared.job.config.docker.DockerContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static simpleci.shared.JsonUtils.jsonToListOfString;

public class JobConfigFactory
{
    public static JobConfig normalize(JsonObject json) throws JobConfigurationException {
        try {
            DockerContainer container = DockerContainerFactory.normalize(json.get("container").getAsJsonObject());
            CacheConfig cache = CacheConfigFactory.normalize(json);
            List<EnvironmentVar> environment = normalizeEnvironment(json);
            List<String> beforeScript = normalizeCommands(json, "before_script");
            List<String> script = normalizeCommands(json, "script");
            List<String> afterSuccess = normalizeCommands(json, "after_success");
            List<String> afterFailure = normalizeCommands(json, "after_failure");
            List<String> afterScript = normalizeCommands(json, "after_script");
            return new JobConfig(container, environment, cache, beforeScript, script, afterSuccess, afterFailure, afterScript);
        } catch(RuntimeException e) {
            throw new JobConfigurationException(e);
        }
    }

    private static List<String> normalizeCommands(JsonObject json, String section) throws JobConfigurationException {
        if (json.has(section)) {
            JsonArray commands = json.get(section).getAsJsonArray();
            return jsonToListOfString(commands);
        }
        return new ArrayList<>();
    }

    private static List<EnvironmentVar> normalizeEnvironment(JsonObject json) throws JobConfigurationException {
        return json.has("env") ? normalizeEnv(json.get("env")) : new ArrayList<>();
    }

    public static List<EnvironmentVar> normalizeEnv(JsonElement env) throws JobConfigurationException {
        if (env instanceof JsonArray) {
            List<String> envStringList = jsonToListOfString((JsonArray) env);
            return parseEnv(envStringList);
        } else if (env instanceof JsonPrimitive) {
            String[] envParts = env.getAsString().split(" ");
            return parseEnv(new ArrayList<>(Arrays.asList(envParts)));
        } else {
            throw new JobConfigurationException("env section must be array or string");
        }
    }

    private static List<EnvironmentVar> parseEnv(List<String> envStringList) throws JobConfigurationException {
        List<EnvironmentVar> envList = new ArrayList<>(envStringList.size());
        for(String envString : envStringList) {
            String[] envVarParts = envString.split("=", 2);
            if(envVarParts.length != 2) {
                throw new JobConfigurationException("Invalid env var format: " + envString);
            }
            envList.add(new EnvironmentVar(envVarParts[0], envVarParts[1]));
        }
        return envList;

    }
}
