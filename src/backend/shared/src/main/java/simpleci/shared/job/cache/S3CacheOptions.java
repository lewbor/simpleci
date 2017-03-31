package simpleci.shared.job.cache;

import com.google.gson.JsonObject;

public class S3CacheOptions extends CacheOptions {
    public final String endPoint;
    public final String accessKey;
    public final String secretKey;
    public final String bucketName;

    public S3CacheOptions(String endPoint,
                          String bucketName,
                          String accessKey,
                          String secretKey) {
        super(CacheType.AMAZON_S3);
        this.endPoint = endPoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
    }
    public S3CacheOptions(JsonObject json) {
        super(CacheType.AMAZON_S3);
        JsonObject options = json.get("options").getAsJsonObject();

        endPoint = options.get("end_point").getAsString();
        accessKey = options.get("access_key").getAsString();
        secretKey = options.get("secret_key").getAsString();
        bucketName = options.get("bucket_name").getAsString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject options = new JsonObject();
        options.addProperty("end_point", endPoint);
        options.addProperty("access_key", accessKey);
        options.addProperty("secret_key", secretKey);
        options.addProperty("bucket_name", bucketName);

        JsonObject message = new JsonObject();
        message.addProperty("type", type);
        message.add("options", options);
        return message;
    }
}
