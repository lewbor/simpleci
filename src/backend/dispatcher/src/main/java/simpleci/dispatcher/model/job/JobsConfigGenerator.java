package simpleci.dispatcher.model.job;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.model.entity.Build;
import simpleci.shared.JsonUtils;
import simpleci.shared.job.JobConfig;
import simpleci.shared.job.config.JobConfigurationException;
import simpleci.shared.job.config.factory.JobConfigFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JobsConfigGenerator {
    private final static Logger logger = LoggerFactory.getLogger(JobsConfigGenerator.class);

    public List<JobConfig> generateJobsConfig(Build build, JsonObject buildConfigJson, String stage) throws JobConfigurationException {
        if (!buildConfigJson.has(stage)) {
            logger.info(String.format("Config for build %d does not contains section %s, skipping", build.id, stage));
            return new ArrayList<>();
        }

        JsonObject stateConfig = buildConfigJson.get(stage).getAsJsonObject();
        return configForState(stateConfig, build);
    }

    private List<JobConfig> configForState(JsonObject stateConfig, Build build) throws JobConfigurationException {
        if (!stateConfig.has("matrix")) {
            logger.error("Job section must contain a matrix");
            return new ArrayList<>();
        }

        JsonArray matrix = stateConfig.get("matrix").getAsJsonArray();
        stateConfig.remove("matrix");

        List<JsonElement> validMatrixCells = filterValidCells(matrix, build);
        List<JsonObject> jobsConfig = new ArrayList<>();
        for (JsonElement matrixCell : validMatrixCells) {
            JsonObject jobConfig = JsonUtils.deepClone(stateConfig).getAsJsonObject();
            jobConfig.add("container", matrixCell);
            jobsConfig.add(jobConfig);
        }
        List<JobConfig> configs = new ArrayList<>(jobsConfig.size());
        for(JsonObject jobConfig : jobsConfig) {
            configs.add(JobConfigFactory.normalize(jobConfig));
        }
        return configs;
    }

    private List<JsonElement> filterValidCells(JsonArray matrix, Build build) {
        List<JsonElement> validCells = new ArrayList<>();
        for (JsonElement matrixElem : matrix) {
            if (isValidCell(matrixElem.getAsJsonObject(), build)) {
                validCells.add(matrixElem);
            }
        }
        return validCells;
    }

    private boolean isValidCell(JsonObject matrixCell, Build build) {
        if (!matrixCell.has("on")) {
            return true;
        }

        JsonObject onCond = matrixCell.get("on").getAsJsonObject();
        if (onCond.has("branch")) {
            JsonElement branchCond = onCond.get("branch");
            Set<String> allowedBranches;
            if (branchCond.isJsonPrimitive()) {
                allowedBranches = ImmutableSet.of(branchCond.getAsString());
            } else if(branchCond.isJsonArray()) {
                allowedBranches = ImmutableSet.copyOf(JsonUtils.jsonArrayToStringList(branchCond.getAsJsonArray()));
            } else {
                return false;
            }
            if (!allowedBranches.contains(build.branch)) {
                return false;
            }

        }
        return true;
    }
}
