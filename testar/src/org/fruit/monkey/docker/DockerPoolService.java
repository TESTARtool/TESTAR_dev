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
    String startWithImage(String imageId, String name, HostConfig hostConfig, String[] env);
    String startWithImage(String imageId, String name, HostConfig hostConfig, String[] env, StrategyIfPresent sip);
    void dispose(boolean alsoRemoveImages);

    String getServiceId();
    String getNetworkId();
    void setDelegate(DockerPoolServiceDelegate delegate);
    DockerPoolServiceDelegate getDelegate();

    public enum StrategyIfPresent { REUSE, RESTART, REINSTALL }
}
