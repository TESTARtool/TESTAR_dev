package org.fruit.monkey.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.ResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DockerPoolServiceImpl implements DockerPoolService {

    final String CONTAINER_NAME_PREFIX = "testar";

    private String serviceId;
    private DockerPoolServiceDelegate delegate;

    private DockerHttpClient dockerHttpClient;
    private DockerClient dockerClient;
    private Map<String, String> containerIds;
    private Set<String> imageIds;
    private String networkId;

    private static final String WINDOWS_DOCKER_HOST = "tcp://localhost:2375";

    final static HashSet<DockerPoolServiceImpl> registry = new HashSet<>();

    public DockerPoolServiceImpl() {
        containerIds = new HashMap<>();
        imageIds = new HashSet<>();

        final DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(detectDockerHost(dockerConfig))
                .responseTimeout(Duration.of(1, ChronoUnit.HOURS))
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(100)
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerConfig, dockerHttpClient);

        registry.add(this);
    }

    private URI detectDockerHost(DockerClientConfig dockerConfig) {
        URI dockerHost;
        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                dockerHost = new URI(WINDOWS_DOCKER_HOST);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            dockerHost = dockerConfig.getDockerHost();
        }
        return dockerHost;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getNetworkId() {
        return networkId;
    }

  public void setDelegate(DockerPoolServiceDelegate delegate) {
        this.delegate = delegate;
    }

    public DockerPoolServiceDelegate getDelegate() {
        return delegate;
    }

    public synchronized void start(String name) {
      String fullName = String.format("%s-%s", CONTAINER_NAME_PREFIX, name);
      this.serviceId = fullName;
      final String networkName = fullName;
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
            e.printStackTrace();
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

        System.out.println("OUT: "+destination);
        final String imageId = dockerClient.buildImageCmd(destination).exec(new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                if (delegate != null) {
                    String status = item.getStatus();
                    Long currentProgress = null;
                    Long totalProgress = null;
                    ResponseItem.ProgressDetail progressDetail = item.getProgressDetail();
                    if (progressDetail != null) {
                        currentProgress = progressDetail.getCurrent();
                        totalProgress = progressDetail.getTotal();
                    }
                    delegate.onStatusChange(status, currentProgress, totalProgress);
                }
                super.onNext(item);
            }
        }).awaitImageId(1, TimeUnit.HOURS);

        if (delegate != null) {
            delegate.onStatusChange("Finalizing", null, null);
        }

        imageIds.add(imageId);

        return imageId;
    }

    public synchronized String startWithImage(String imageId, String name, HostConfig hostConfig) {
        return startWithImage(imageId, name, hostConfig, null, StrategyIfPresent.REUSE);
    }

    public synchronized String startWithImage(String imageId, String name, HostConfig hostConfig, String[] env) {
      return startWithImage(imageId, name, hostConfig, env, StrategyIfPresent.REUSE);
    }

    public synchronized String startWithImage(String imageId, String name, HostConfig hostConfig, String[] env, StrategyIfPresent sip) {
        String fullName = String.format("%s-%s", CONTAINER_NAME_PREFIX, name);

        String containerId = containerIds.get(fullName);
        if (containerId != null) {
            return containerId;
        }

        boolean containerStarted = false;

        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List <Container> containerList = listContainersCmd.withNameFilter(Collections.singleton(fullName)).withShowAll(true).exec();
        if (containerList.size() > 0) {
          Container container = containerList.get(0);
          containerId = container.getId();

//          System.out.println("Container state: " + );
          boolean containerIsRunning = container.getState().toLowerCase(Locale.ROOT).contains("running");

          switch (sip) {
            case REUSE:
              if (!containerIsRunning) {
                dockerClient.startContainerCmd(containerId).exec();
              }
              containerStarted = true;
              break;
            case RESTART:
              dockerClient.restartContainerCmd(containerId).exec();
              containerStarted = true;
              break;
            case REINSTALL:
              if (containerIsRunning) {
//                System.out.println("Cont")
                dockerClient.stopContainerCmd(containerId).exec();
              }
              dockerClient.removeContainerCmd(containerId).exec();
              break;
          }
        }

        if (!containerStarted) {
          final CreateContainerCmd cmd = dockerClient.createContainerCmd(imageId)
            .withName(fullName)
            .withHostName(fullName)
            .withHostConfig(hostConfig);
          final CreateContainerResponse containerResponse =
            (env == null ? cmd.exec() : cmd.withEnv(env).exec());
          containerId = containerResponse.getId();

          containerIds.put(fullName, containerId);
          dockerClient.connectToNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();
          dockerClient.startContainerCmd(containerId).exec();
        }

        return containerId;
    }

    private void disposeInternal(boolean alsoRemoveImages) {
        for (Container container : dockerClient.listContainersCmd().withShowAll(true).exec()) {
            final String containerId = container.getId();
            if (containerIds.values().contains(containerId)) {
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
