package org.fruit.monkey.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DockerPoolServiceImpl implements DockerPoolService {

    private String serviceId;

    private DockerHttpClient dockerHttpClient;
    private DockerClient dockerClient;
    private Set<String> containerIds;
    private Set<String> imageIds;
    private String networkId;

    final static HashSet<DockerPoolServiceImpl> registry = new HashSet<>();

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

        registry.add(this);
    }

    public void start(String serviceId) {
        this.serviceId = serviceId;
        final String networkName = "testar_" + serviceId;
        List<Network> dockerNetworks = dockerClient.listNetworksCmd().withNameFilter(networkName).exec();
        if(dockerNetworks.size() > 0) {
            this.networkId = dockerNetworks.get(0).getId();
        } else {
            CreateNetworkResponse networkResponse = dockerClient.createNetworkCmd().withName(networkName).withDriver("bridge").withAttachable(true).exec();
            this.networkId = networkResponse.getId();
        }
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

        System.out.println("OUT"+destination);
        final String imageId = dockerClient.buildImageCmd(destination).exec(new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                String status = item.getStatus();
                if(status == null) {
                    status = "N/A";
                }
                Long currentProgress = null;
                Long totalProgress = null;
                ResponseItem.ProgressDetail progressDetail = item.getProgressDetail();
                if (progressDetail != null) {
                    currentProgress = progressDetail.getCurrent();
                    totalProgress = progressDetail.getTotal();
                }
                if (currentProgress == null) {
                    currentProgress = 0L;
                }
                if (totalProgress == null) {
                    totalProgress = 0L;
                }
                System.out.println("Status: " + status + ", progress: " + currentProgress + " of " + totalProgress);
//                System.out.println(item);
                super.onNext(item);
            }
        }).awaitImageId();
        imageIds.add(imageId);

        return imageId;
    }

    public String startWithImage(String imageId, String name, HostConfig hostConfig) {
        return startWithImage(imageId, name, hostConfig, null);
    }

    public String startWithImage(String imageId, String name, HostConfig hostConfig, String[] env) {
        final CreateContainerCmd cmd = dockerClient.createContainerCmd(imageId)
                .withName(name)
                .withHostName(name)
                .withHostConfig(hostConfig);
        final CreateContainerResponse containerResponse =
                (env == null ? cmd.exec() : cmd.withEnv(env).exec());
        final String containerId = containerResponse.getId();
        containerIds.add(containerId);
        dockerClient.connectToNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();
        dockerClient.startContainerCmd(containerId).exec();

        return containerId;
    }

    private void disposeInternal(boolean alsoRemoveImages) {
        for (Container container : dockerClient.listContainersCmd().withShowAll(true).exec()) {
            final String containerId = container.getId();
            if (containerIds.contains(containerId)) {
                System.out.println("Container ID: " + containerId);
                if (container.getState().toLowerCase(Locale.ROOT).contains("running")) {
                    dockerClient.killContainerCmd(containerId).exec();
                }
                String imageId = container.getImageId();
                System.out.println("Image ID: " + imageId);
                if (imageId.contains(":")) {
                    imageId = imageId.substring(imageId.indexOf(':') + 1);
                }
                final RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(containerId).withForce(true).withRemoveVolumes(true);
                removeContainerCmd.exec();
//                try {
//                    removeContainerCmd.wait();
//                }
//                catch(InterruptedException e) {}
//                finally {
                    dockerClient.removeImageCmd(imageId).withForce(true).exec();
//                }
            }
        }
        containerIds.clear();
        if (alsoRemoveImages) {
            for (String imageId : imageIds) {
                dockerClient.removeImageCmd(imageId).withForce(true).exec();
            }
            imageIds.clear();
        }

        if (networkId != null) {
            dockerClient.removeNetworkCmd(networkId).exec();
            networkId = null;
        }

        try {
            dockerClient.close();
        } catch (Exception e) {
        }
    }

    public void dispose(boolean alsoRemoveImages) {
        disposeInternal(alsoRemoveImages);
        registry.remove(this);
    }

    public static void disposeAll(boolean alsoRemoveImages) {
        for (DockerPoolServiceImpl instance: registry) {
            if (instance.isDockerAvailable()) {
                instance.disposeInternal(alsoRemoveImages);
            }
        }
        registry.clear();
    }
}
