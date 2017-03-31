package simpleci.shared.job;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class RepositorySettings {
    public final String repositoryUrl;
    public final String commit;
    public final String commitRange;
    public final String branch;
    public final String tag;

    public RepositorySettings(
            String repositoryUrl,
            String commit,
            String commitRange,
            String branch,
            String tag) {
        this.repositoryUrl = repositoryUrl;
        this.commit = commit;
        this.commitRange = commitRange;
        this.branch = branch;
        this.tag = tag;
    }

    public RepositorySettings(JsonObject json) {
        repositoryUrl = json.get("repository_url").getAsString();
        commit = json.get("commit").getAsString();
        commitRange = json.get("commit_range").getAsString();
        branch = json.get("branch").getAsString();
        tag = json.get("tag") instanceof JsonNull ? null : json.get("tag").getAsString();
    }

    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("repository_url", repositoryUrl);
        message.addProperty("commit", commit);
        message.addProperty("commit_range", commitRange);
        message.addProperty("branch", branch);
        message.addProperty("tag", tag);
        return message;
    }
}
