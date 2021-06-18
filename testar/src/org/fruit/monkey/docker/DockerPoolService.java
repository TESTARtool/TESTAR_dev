package org.fruit.monkey.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.HostConfig;

import java.io.File;
import java.io.IOException;

public interface DockerPoolService {
    boolean isDockerAvailable();
    void start(String serviceId);
    DockerClient getClient();
    String buildImage(File destination, String dockerFileContent) throws IOException;
    String startWithImage(String imageId, String name, HostConfig hostConfig);
    public String startWithImage(String imageId, String name, HostConfig hostConfig, String[] env);
    void dispose(boolean alsoRemoveImages);
}
