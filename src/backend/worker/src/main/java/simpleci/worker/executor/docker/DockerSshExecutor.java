package simpleci.worker.executor.docker;


import com.google.common.io.Files;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.spotify.docker.client.DockerException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.job.config.docker.DockerContainer;
import simpleci.worker.job.JobContext;
import simpleci.worker.system.DockerContainerManager;
import simpleci.worker.system.SshClient;
import simpleci.worker.bash.BuildScriptMaker;
import simpleci.worker.bash.Locations;
import simpleci.worker.cache.*;
import simpleci.worker.executor.Executor;
import simpleci.worker.job.JobOutputProcessor;
import simpleci.worker.job.JobStatus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class DockerSshExecutor implements Executor {
    private final Logger logger = LoggerFactory.getLogger(DockerSshExecutor.class);
    private static final int BUILD_SCRIPT_MODE = 0775;

    private final DockerContainerManager containerManager;
    private ExecutorContext executorContext;


    public DockerSshExecutor(DockerContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    public String execute(JobContext context) {
        executorContext = new ExecutorContext();
        try {
            prepareContainers(context);
            if(executorContext.executionWasStopped) {
                return JobStatus.STOPPED;
            }

            String buildScript = new BuildScriptMaker().generateBuildScript(context);
            uploadBuildScript(executorContext.sshClient, buildScript);
            if (context.jobConfig.cache.pull) {
                fetchAndUploadCache(context, executorContext.sshClient);
            }
            if(executorContext.executionWasStopped) {
                return JobStatus.STOPPED;
            }

            String status = runBuildScript(executorContext.sshClient, context.outputProcessor);
            if(executorContext.executionWasStopped) {
                return JobStatus.STOPPED;
            }

            if (context.jobConfig.cache.push) {
                downloadAndPushCache(context, executorContext.sshClient);
            }
            return executorContext.executionStatus(status);
        } catch (DockerException e) {
            context.outputProcessor.output("Docker error: " + e.getMessage());
            return executorContext.executionStatus(JobStatus.FAILED);
        } catch (JSchException | SftpException | IOException e) {
            logger.error("Error processing build", e);
            return executorContext.executionStatus(JobStatus.FAILED);
        } finally {
            cleanup();
        }
    }

    private void prepareContainers(JobContext context) throws DockerException, JSchException {
        String buildContainerId = runContainerRecursice(context.jobConfig.container, context.jobSettings.info.jobId);

        String buildContainerIpAddress = containerManager.containerIp(buildContainerId);
        executorContext.sshClient = new SshClient(buildContainerIpAddress, "simpleci", "simpleci");
    }

    @Override
    public void stop() {
        executorContext.executionWasStopped = true;
        executorContext.sshClient.close();
    }

    private void downloadAndPushCache(JobContext context, SshClient sshClient) {
        CacheManager cacheManager = CacheFactory.fromJobContext(context);
        if (sshClient.fileExist(Locations.CACHE_FILE)) {
            String tmpCacheFileName = "cache-" + UUID.randomUUID() + ".tar.gz";
            File tmpDir = Files.createTempDir();
            String tmpCacheFilePath = Paths.get(tmpDir.getAbsolutePath(), tmpCacheFileName).toString();

            try {
                sshClient.downloadFile(Locations.CACHE_FILE, tmpCacheFilePath);
                cacheManager.uploadCache(context.outputProcessor, tmpCacheFilePath);
            } catch (SftpException | JSchException e) {
                context.outputProcessor.output("Error upload cache: " + e.getMessage() + "\n");
            } finally {
                try {
                    FileUtils.deleteDirectory(tmpDir);
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }


    private void fetchAndUploadCache(JobContext context, SshClient sshClient) {
        CacheManager cacheManager = CacheFactory.fromJobContext(context);
        cacheManager.downloadCache(context.outputProcessor, cachePath -> CacheUtils.uploadCacheToDocker(sshClient, cachePath));
    }

    private String runBuildScript(SshClient sshClient, final JobOutputProcessor outputProcessor) throws IOException, JSchException {
        int exitCode = sshClient.executeCmd(String.format("%s 2>&1", Locations.BUILD_SCRIPT), outputProcessor);
        logger.info(String.format("Build script finished with exit code %d", exitCode));
        return exitCode == 0 ? JobStatus.FINISHED_SUCCESS : JobStatus.FAILED;
    }

    private void uploadBuildScript(SshClient sshClient, String buildScript) throws SftpException, JSchException {
        sshClient.uploadFile(new ByteArrayInputStream(buildScript.getBytes()), Locations.BUILD_SCRIPT, BUILD_SCRIPT_MODE);
    }


    private String runContainerRecursice(DockerContainer container, long jobId) throws DockerException {
        DockerContainerManager.RunResult runResult = containerManager.runContainerRecursive(container, jobId);
        executorContext.containers.addAll(runResult.containers);
        if(!runResult.success) {
            throw runResult.e;
        }

        return runResult.containers.get(runResult.containers.size() - 1);
    }


    private void cleanup() {
        if(executorContext.sshClient != null) {
            executorContext.sshClient.close();
            executorContext.sshClient = null;
        }

        for (String serviceContainer : executorContext.containers) {
            logger.info(String.format("Stopping container %s", serviceContainer));
            containerManager.stopAndRemove(serviceContainer);
        }
        executorContext.containers.clear();
    }
}
