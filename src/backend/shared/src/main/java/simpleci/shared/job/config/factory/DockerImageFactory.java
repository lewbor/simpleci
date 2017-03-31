package simpleci.shared.job.config.factory;

import simpleci.shared.job.config.JobConfigurationException;
import simpleci.shared.job.config.docker.DockerImage;

public class DockerImageFactory {

    public static DockerImage normalize(String imageDescription) throws JobConfigurationException {
        String[] imageParts = imageDescription.split(":");
        switch (imageParts.length) {
            case 1:
                return new DockerImage(imageParts[0], "latest");
            case 2:
                return new DockerImage(imageParts[0], imageParts[1]);
            default:
                throw new JobConfigurationException(String.format("Malformed name name: %s", imageDescription));
        }
    }
}
