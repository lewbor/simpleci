package simpleci.worker.system;

public final class Naming {

    public static String containerName(long jobId, String service) {
        return String.format("simpleci-%d-%s", jobId, service.replaceAll("/", "-"));
    }

}
