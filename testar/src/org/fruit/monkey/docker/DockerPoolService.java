package org.fruit.monkey.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.HostConfig;

import java.io.File;
import java.io.IOException;

public interface DockerPoolService {
    boolean isDockerAvailable();
    DockerClient getClient();
    String buildImage(File destination, String dockerFileContent) throws IOException;
    String startWithImage(String imageId, String name, HostConfig hostConfig);
    void dispose(boolean alsoRemoveImages);
}
