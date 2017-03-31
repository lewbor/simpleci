package simpleci.shared.job.config.docker;

import com.google.gson.JsonObject;
import simpleci.shared.job.config.EnvironmentVar;

import java.util.ArrayList;
import java.util.List;

import static simpleci.shared.JsonUtils.*;

public class DockerContainer {
    public final DockerImage image;
    public final boolean privileged;
    public final List<EnvironmentVar> environment;
    public final List<String> volumes;
    public final List<DockerLink> links;

    public DockerContainer(
            DockerImage image,
            boolean privileged,
            List<EnvironmentVar> environment,
            List<String> volumes,
            List<DockerLink> links) {
        this.image = image;
        this.privileged = privileged;
        this.environment = environment;
        this.volumes = volumes;
        this.links = links;
    }

    public DockerContainer(DockerImage image) {
        this(image, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public DockerContainer(JsonObject json)  {
        image = new DockerImage(json.get("image").getAsJsonObject());
        privileged = json.get("privileged").getAsBoolean();
        volumes = jsonToListOfString(json.get("volumes").getAsJsonArray());
        environment = jsonToList(json.get("environment").getAsJsonArray(), jsonElement -> new EnvironmentVar(jsonElement.getAsJsonObject()));
        links = jsonToList(json.get("links").getAsJsonArray(), jsonElement -> new DockerLink(jsonElement.getAsJsonObject()));
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("image", image.toJson());
        json.addProperty("privileged", privileged);
        json.add("volumes", listOfString(volumes));
        json.add("environment", listOfObjects(environment, EnvironmentVar::toJson));
        json.add("links", listOfObjects(links, DockerLink::toJson));
        return json;
    }


}
