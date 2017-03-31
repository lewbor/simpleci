package simpleci.worker.system;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.ImageNotFoundException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.shared.job.config.EnvironmentVar;
import simpleci.shared.job.config.docker.DockerContainer;
import simpleci.shared.job.config.docker.DockerImage;
import simpleci.shared.job.config.docker.DockerLink;

import java.util.ArrayList;
import java.util.List;

public class DockerContainerManager {
    private final Logger logger = LoggerFactory.getLogger(DockerContainerManager.class);

    private DockerClient dockerClient;

    public DockerContainerManager(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public String runContainer(ContainerConfig.Builder containerConfigBuilder, String containerName) throws DockerException {
        try {
            final ContainerConfig containerConfig = containerConfigBuilder.build();
            final ContainerCreation creation;

            creation = dockerClient.createContainer(containerConfig, containerName);

            final String id = creation.id();
            dockerClient.startContainer(id);
            return id;
        } catch (InterruptedException e) {
            logger.error("", e);
        }

        return null;
    }

    private void createImage(DockerImage image) throws DockerException {
        try {
            try {
                dockerClient.inspectImage(image.name());
            } catch (ImageNotFoundException e) {
                logger.info(String.format("Image %s is absent, try pulling it", image.name()));
                dockerClient.pull(image.name());
            }
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    public void stopAndRemove(String containerId) {
        try {
            dockerClient.stopContainer(containerId, 10);
            dockerClient.removeContainer(containerId);
        } catch (DockerException | InterruptedException e) {
            logger.error("", e);
        }
    }

    public RunResult runContainerRecursive(DockerContainer container, long jobId) throws DockerException {
        createImage(container.image);

        ContainerConfig.Builder containerBuilder = ContainerConfig.builder();
        containerBuilder
                .image(container.image.name());

        HostConfig.Builder hostConfigBuilder = HostConfig.builder();
        if(container.privileged) {
            hostConfigBuilder.privileged(true);
        }
        if (container.environment.size() > 0) {
            containerBuilder.env(EnvironmentVar.envListToStringList(container.environment));
        }
        if(container.volumes.size() > 0) {
            hostConfigBuilder.binds(container.volumes);
        }

        if (container.links.size() > 0) {
            List<String> links = new ArrayList<>();
            for (DockerLink link : container.links) {
                final String serviceContainerName = Naming.containerName(jobId, link.container.image.name);
                final String linkName = String.format("%s:%s", serviceContainerName, link.alias);
                links.add(linkName);
            }
            hostConfigBuilder.links(links);
        }

        HostConfig hostConfig = hostConfigBuilder.build();
        containerBuilder.hostConfig(hostConfig);

        final List<String> runnedIds = new ArrayList<>();
        for (DockerLink link : container.links) {
            RunResult runResult = runContainerRecursive(link.container, jobId);
            if(runResult.success) {
                runnedIds.addAll(runResult.containers);
            } else {
                return runResult;
            }
        }

        try {
            String id = runContainer(containerBuilder, Naming.containerName(jobId, container.image.name));
            runnedIds.add(id);

            return new RunResult(runnedIds);
        } catch(DockerException e) {
           return new RunResult(runnedIds, e);
        }
    }

    public String containerIp(String buildContainerId) {
        try {
            return dockerClient.inspectContainer(buildContainerId).networkSettings().ipAddress();
        } catch (DockerException | InterruptedException e) {
            logger.error("", e);
        }
        return null;
    }

    public class RunResult {
        public final List<String> containers;
        public final boolean success;
        public final DockerException e;

        RunResult(List<String> containers) {
            this.containers = containers;
            this.success = true;
            this.e = null;
        }

        RunResult(List<String> containers, DockerException e) {
            this.containers = containers;
            this.success = false;
            this.e = e;
        }
    }
}
