package simpleci.shared.job;

import com.google.gson.JsonObject;
import simpleci.shared.job.cache.*;

public class JobSettings {
    public final JobInfo info;
    public final RepositorySettings repositorySettings;
    public final SshKeyOptions sshKey;
    public final CacheOptions cacheOptions;

    public JobSettings(
            JobInfo info,
            RepositorySettings repositorySettings,
            SshKeyOptions sshKey,
            CacheOptions cacheOptions) {
        this.info = info;
        this.repositorySettings = repositorySettings;
        this.sshKey = sshKey;
        this.cacheOptions = cacheOptions;
    }

    public JobSettings(JsonObject json) {
        info = new JobInfo(json.get("info").getAsJsonObject());
        repositorySettings = new RepositorySettings(json.get("repository_settings").getAsJsonObject());
        sshKey = new SshKeyOptions(json.get("ssh_key").getAsJsonObject());
        cacheOptions = cacheSettings(json.getAsJsonObject("cache_settings"));
    }

    private CacheOptions cacheSettings(JsonObject cacheSettings) {
        String cacheType = cacheSettings.get("type").getAsString();
        switch (cacheType) {
            case CacheType.AMAZON_S3:
                return new S3CacheOptions(cacheSettings);
              case CacheType.GOOGLE_STORAGE:
                return new GoogleStorageCacheOptions(cacheSettings);
             default:
                return new NoCacheOptions();
        }
    }

    public JsonObject toJson() {
        JsonObject message = new JsonObject();
        message.add("info", info.toJson());
        message.add("repository_settings", repositorySettings.toJson());
        message.add("ssh_key", sshKey.toJson());
        message.add("cache_settings", cacheOptions.toJson());
        return message;
    }

}
