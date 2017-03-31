package simpleci.worker;

import com.google.common.base.MoreObjects;
import simpleci.shared.worker.WorkerType;
import simpleci.shared.worker.options.GceWorkerOptions;
import simpleci.shared.worker.options.LocalWorkerOptions;
import simpleci.shared.worker.options.WorkerOptions;
import simpleci.worker.system.Utils;
import simpleci.worker.executor.Executors;

import java.io.IOException;
import java.util.UUID;

public class AppParameters {
    public String workerId;
    public String workerType;
    public WorkerOptions workerOptions;
    public String executor;
    public String dockerEndpoint;
    public String rabbitmqHost;
    public int rabbitmqPort;
    public String rabbitmqUser;
    public String rabbitmqPassword;
    public boolean exitIfInactive;
    public int minimumRunningTime;
    public int timeGranulatity;

    public static AppParameters fromEnv()  {
        AppParameters parameters = new AppParameters();
        parameters.dockerEndpoint = "unix:///var/run/docker.sock";
        parameters.workerId = getEnv("WORKER_ID", UUID.randomUUID().toString());
        parameters.workerType = getEnv("WORKER_TYPE", "local");
        parameters.executor = getEnv("EXECUTOR", Executors.DOCKER_SSH);
        parameters.rabbitmqHost = getEnv("RABBITMQ_HOST", "localhost");
        parameters.rabbitmqPort = getEnv("RABBITMQ_PORT", 5672);
        parameters.rabbitmqUser = getEnv("RABBITMQ_USER", "guest");
        parameters.rabbitmqPassword = getEnv("RABBITMQ_PASSWORD", "guest");
        parameters.exitIfInactive = getEnv("EXIT_IF_INACTIVE", false);
        parameters.minimumRunningTime = getEnv("MINIMUM_RUNNING_TIME", 60 * 10 - 30);
        parameters.timeGranulatity = getEnv("TIME_GRANULARITY", 60);
        parameters.workerOptions = workerOptions(parameters);
        return parameters;
    }

    private static WorkerOptions workerOptions(AppParameters parameters) {
        switch (parameters.workerType) {
            case WorkerType.LOCAL:
                return new LocalWorkerOptions();
            case WorkerType.GCE:
                int providerId = getEnv("GCE_PROVIDER_ID");
                return new GceWorkerOptions(providerId);
            default:
                throw new RuntimeException(String.format("Unknown worker type: %s", parameters.workerType));
        }

    }

    private static boolean getEnv(String name, boolean defaultValue) {
        String envValue = System.getenv(name);
        if (envValue != null) {
            return Boolean.parseBoolean(envValue);
        }
        return defaultValue;
    }

    private static int getEnv(String name, int defaultValue) {
        String envValue = System.getenv(name);
        if (envValue != null) {
            return Integer.parseInt(envValue);
        }
        return defaultValue;
    }

    private static int getEnv(String name) {
        String envValue = System.getenv(name);
        if (envValue != null) {
            return Integer.parseInt(envValue);
        }
        throw new RuntimeException(String.format("Env var is not set: %s", name));
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
        return MoreObjects.toStringHelper(getClass())
                .add("worker_id", workerId)
                .add("worker_type", workerType)
                .add("worker_options", workerOptions)
                .add("rabbitmq_host", rabbitmqHost)
                .add("rabbitmq_port", rabbitmqPort)
                .add("rabbitmq_user", rabbitmqUser)
                .add("rabbitmq_password", rabbitmqPassword)
                .add("exit_if_inactive", exitIfInactive)
                .add("minimum_running_time", minimumRunningTime)
                .add("time_granularity", timeGranulatity)
                .toString();
    }
}
