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
import simpleci.shared.job.config.docker.DockerImage;
import simpleci.shared.job.config.docker.DockerLink;

import java.util.ArrayList;
import java.util.List;

public class DockerContainerFactory
{
    public static DockerContainer normalize(JsonObject json) throws JobConfigurationException {
        if (!json.has("image")) {
            throw new JobConfigurationException("name option does not exists in build config");
        }

        String imageName = json.get("image").getAsString();
        DockerImage image = DockerImageFactory.normalize(imageName);

        List<EnvironmentVar> environment;
        if (json.has("env")) {
            environment = JobConfigFactory.normalizeEnv(json.get("env"));
        } else {
            environment = new ArrayList<>();
        }

        boolean privileged = CacheConfigFactory.defaultValue(json, "privileged", false);

        List<String> volumes = parseVolumes(json);
        List<DockerLink> links = parseLinks(json);

        return new DockerContainer(image, privileged, environment, volumes, links);
    }

    private static List<String> parseVolumes(JsonObject json) {
        List<String> volumes = new ArrayList<>();
        if (!json.has("volumes")) {
            return volumes;
        }

        JsonElement volumesJson = json.get("volumes");
        if (volumesJson instanceof JsonPrimitive) {
            volumes.add(volumesJson.getAsString());
        } else {
            for (JsonElement volumeJson : volumesJson.getAsJsonArray()) {
                volumes.add(volumeJson.getAsString());
            }
        }
        return volumes;

    }

    private static List<DockerLink> parseLinks(JsonObject json) throws JobConfigurationException {
        List<DockerLink> links = new ArrayList<>();
        if (!json.has("links")) {
            return links;
        }

        JsonArray jsonLinks = json.get("links").getAsJsonArray();
        for (JsonElement jsonLink : jsonLinks) {
            links.add(parseLink(jsonLink));
        }
        return links;
    }

    private static DockerLink parseLink(JsonElement jsonLink) throws JobConfigurationException {
        if (jsonLink instanceof JsonPrimitive) {
            DockerContainer container = new DockerContainer(DockerImageFactory.normalize(jsonLink.getAsString()));
            String[] imageNameParts = container.image.name.split("/");
            String linkAlias = imageNameParts[imageNameParts.length - 1];
            return new DockerLink(container, linkAlias);
        } else if (jsonLink instanceof JsonObject) {
            JsonObject linkDescription = (JsonObject) jsonLink;
            DockerContainer container = DockerContainerFactory.normalize(linkDescription);
            String linkAlias;
            if (linkDescription.has("alias")) {
                linkAlias = linkDescription.get("alias").getAsString();
            } else {
                String[] imageNameParts = container.image.name.split("/");
                linkAlias = imageNameParts[imageNameParts.length - 1];
            }
            return new DockerLink(container, linkAlias);
        } else {
            throw new JobConfigurationException("Link item must be string or object: " + jsonLink);
        }
    }
}
