package simpleci.dispatcher;

import com.google.common.base.MoreObjects;

public class AppParameters {
    public String rabbitmqHost;
    public int rabbitmqPort;
    public String rabbitmqUser;
    public String rabbitmqPassword;

    public String redisHost;
    public int redisPort;

    public String databaseHost;
    public int databasePort;
    public String databaseUser;
    public String databasePassword;
    public String databaseName;

    public static AppParameters fromEnv() {
        AppParameters parameters = new AppParameters();

        parameters.rabbitmqHost = getEnv("RABBITMQ_HOST", "localhost");
        parameters.rabbitmqPort = getEnv("RABBITMQ_PORT", 5672);
        parameters.rabbitmqUser = getEnv("RABBITMQ_USER", "guest");
        parameters.rabbitmqPassword = getEnv("RABBITMQ_PASSWORD", "guest");

        parameters.redisHost = getEnv("REDIS_HOST", "localhost");
        parameters.redisPort = getEnv("REDIS_PORT", 6379);

        parameters.databaseHost = getEnv("DATABASE_HOST", "localhost");
        parameters.databasePort = getEnv("DATABASE_PORT", 3306);
        parameters.databaseUser = getEnv("DATABASE_USER", "root");
        parameters.databasePassword = getEnv("DATABASE_PASSWORD", "");
        parameters.databaseName = getEnv("DATABASE_NAME", "simpleci");

        return parameters;
    }

    private static int getEnv(String name, int defaultValue) {
        String envValue = System.getenv(name);
        if (envValue != null) {
            return Integer.parseInt(envValue);
        }
        return defaultValue;
    }

    private static String getEnv(String name, String defaultValue) {
        String envValue = System.getenv(name);
        if (envValue != null) {
            return envValue;
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(getClass())
                .add("redis_host", redisHost)
                .add("redis_port", redisPort)
                .add("database_host", databaseHost)
                .add("database_port", databasePort)
                .add("database_user", databaseUser)
                .add("database_password", databasePassword)
                .add("database_name", databaseName)
                .add("rabbitmq_host", rabbitmqHost)
                .add("rabbitmq_port", rabbitmqHost)
                .add("rabbitmq_user", rabbitmqUser)
                .add("rabbitmq_password", rabbitmqPassword)
                .toString();
    }

}
