package simpleci.worker.executor.shell;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.worker.bash.BuildScriptMaker;
import simpleci.worker.bash.Locations;
import simpleci.worker.cache.*;
import simpleci.worker.executor.Executor;
import simpleci.worker.job.JobContext;
import simpleci.worker.job.JobOutputProcessor;
import simpleci.worker.job.JobStatus;

import java.io.*;

public class ShellExecutor implements Executor {
    private final Logger logger = LoggerFactory.getLogger(ShellExecutor.class);

    @Override
    public String execute(JobContext context) {
        try {
            File buildDir = new File(Locations.HOME_DIR);
            buildDir.mkdirs();
            FileUtils.cleanDirectory(buildDir);

            File buildScriptFile = new File(Locations.BUILD_SCRIPT);

            buildScriptFile.createNewFile();
            buildScriptFile.setExecutable(true);

            String buildScript = new BuildScriptMaker().generateBuildScript(context);
            try (PrintWriter out = new PrintWriter(buildScriptFile)) {
                out.println(buildScript);
            }

            if (context.jobConfig.cache.pull) {
                fetchAndUploadCache(context);
            }

            String status = runBuildScript(context.outputProcessor);

            if (context.jobConfig.cache.push) {
                downloadAndPushCache(context);
            }
            return status;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return JobStatus.FAILED;
        }
    }

    private String runBuildScript(final JobOutputProcessor outputProcessor) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder( Locations.BUILD_SCRIPT);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            outputProcessor.output(line);
        }
        int exitCode = process.waitFor();
        logger.info(String.format("Build script finished with exit code %d", exitCode));
        return exitCode == 0 ? JobStatus.FINISHED_SUCCESS : JobStatus.FAILED;
    }

    private void fetchAndUploadCache(JobContext context) {
        CacheManager cacheManager = CacheFactory.fromJobContext(context);
        cacheManager.downloadCache(context.outputProcessor, cachePath -> FileUtils.copyFile(new File(cachePath), new File(Locations.CACHE_FILE)));
    }

    private void downloadAndPushCache(JobContext context) throws IOException {
        CacheManager cacheManager = CacheFactory.fromJobContext(context);
        File cacheFile = new File(Locations.CACHE_FILE);
        if (cacheFile.exists()) {
            cacheManager.uploadCache(context.outputProcessor, Locations.CACHE_FILE);
            FileUtils.forceDelete(cacheFile);
        }
    }


    @Override
    public void stop() {
        //todo implement
    }
}
