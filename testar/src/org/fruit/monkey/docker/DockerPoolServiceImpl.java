package org.fruit.monkey.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DockerPoolServiceImpl implements DockerPoolService {

    private String serviceId;

    private DockerHttpClient dockerHttpClient;
    private DockerClient dockerClient;
    private Set<String> containerIds;
    private Set<String> imageIds;
    private String networkId;

    public DockerPoolServiceImpl() {
        containerIds = new HashSet<>();
        imageIds = new HashSet<>();

        final DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(100)
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerConfig, dockerHttpClient);
    }

    public void start(String serviceId) {
        this.serviceId = serviceId;

        CreateNetworkResponse networkResponse = dockerClient.createNetworkCmd().withName("testar_" + serviceId).withDriver("bridge").withAttachable(true).exec();
        networkId = networkResponse.getId();
    }

    public boolean isDockerAvailable() {
        DockerHttpClient.Request request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path("/_ping")
                .build();

        try {
            DockerHttpClient.Response response = dockerHttpClient.execute(request);
            return (response.getStatusCode() == 200);
        }
        catch (Exception e) {
            return false;
        }
    }

    public DockerClient getClient() {
        return dockerClient;
    }

    public String buildImage(File destination, String dockerFileContent) throws IOException {
        if (dockerFileContent != null) {
            File dockerFile;
            if (destination.isDirectory()) {
                dockerFile = new File(destination, "Dockerfile");
            } else {
                dockerFile = destination;
            }

            final FileOutputStream dockerFileStream = new FileOutputStream(dockerFile);
            dockerFileStream.write(dockerFileContent.getBytes(StandardCharsets.UTF_8));
            dockerFileStream.flush();
            dockerFileStream.close();
        }

        final String imageId = dockerClient.buildImageCmd(destination).exec(new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                System.out.println(item);
                super.onNext(item);
            }
        }).awaitImageId();
        imageIds.add(imageId);

        return imageId;
    }

    public String startWithImage(String imageId, String name, HostConfig hostConfig) {
        final CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageId)
                .withName(name)
                .withHostName(name)
                .withHostConfig(hostConfig)
                .exec();
        final String containerId = containerResponse.getId();
        containerIds.add(containerId);
        dockerClient.connectToNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();
        dockerClient.startContainerCmd(containerId).exec();

        return containerId;
    }

    public void dispose(boolean alsoRemoveImages) {
        for (Container container: dockerClient.listContainersCmd().withShowAll(true).exec()) {
            final String containerId = container.getId();
            if (containerIds.contains(containerId)) {
                if (container.getState().toLowerCase(Locale.ROOT).contains("running")) {
                    dockerClient.killContainerCmd(containerId).exec();
                }
                final String imageId = container.getImageId();
                dockerClient.removeContainerCmd(containerId).withForce(true).withRemoveVolumes(true).exec();
                dockerClient.removeImageCmd(imageId);
            }
        }
        containerIds.clear();
        if (alsoRemoveImages) {
            for (String imageId: imageIds) {
                dockerClient.removeImageCmd(imageId).withForce(true).exec();
            }
            imageIds.clear();
        }
        dockerClient.removeNetworkCmd(networkId).exec();
        networkId = null;
        try {
            dockerClient.close();
        }
        catch (Exception e) {}
    }
}
