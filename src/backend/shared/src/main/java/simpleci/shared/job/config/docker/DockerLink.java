package simpleci.shared.job.config.docker;


import com.google.gson.JsonObject;

public class DockerLink {
    public final DockerContainer container;
    public final String alias;

    public DockerLink(DockerContainer container, String alias) {
        this.container = container;
        this.alias = alias;
    }

    public DockerLink(JsonObject json) {
        alias = json.get("alias").getAsString();
        container = new DockerContainer(json.get("container").getAsJsonObject());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("alias", alias);
        json.add("container", container.toJson());
        return json;
    }
}
