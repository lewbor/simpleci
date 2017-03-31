package simpleci.shared.job.cache;

import com.google.gson.JsonObject;

public class GoogleStorageCacheOptions extends CacheOptions {
    public final String serviceAccount;
    public final String bucketName;

    public GoogleStorageCacheOptions(
            String serviceAccount,
            String bucketName){
        super(CacheType.GOOGLE_STORAGE);
        this.serviceAccount = serviceAccount;
        this.bucketName = bucketName;
    }

    public GoogleStorageCacheOptions(JsonObject json) {
        super(CacheType.GOOGLE_STORAGE);
        JsonObject options = json.get("options").getAsJsonObject();

        serviceAccount = options.get("service_account").getAsString();
        bucketName = options.get("bucket_name").getAsString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject options = new JsonObject();
        options.addProperty("service_account", serviceAccount);
        options.addProperty("bucket_name", bucketName);

        JsonObject message = new JsonObject();
        message.addProperty("type", type);
        message.add("options", options);

        return message;
    }
}
