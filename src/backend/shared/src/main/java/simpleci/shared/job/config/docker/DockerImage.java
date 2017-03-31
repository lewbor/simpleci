package simpleci.shared.job.config.docker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import simpleci.shared.job.config.JobConfigurationException;

public class DockerImage {
    public final String name;
    public final String tag;

    public DockerImage(String image, String tag) {
        this.name = image;
        this.tag = tag;
    }

    public DockerImage(JsonObject json) {
        name = json.get("name").getAsString();
        tag = json.get("tag").getAsString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("tag", tag);
        return json;
    }

    public String name() {
        return String.format("%s:%s", name, tag);
    }
}
