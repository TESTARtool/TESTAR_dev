package org.fruit.monkey.sonarqube;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.io.IOUtils;
import org.fruit.monkey.Main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SonarqubeServiceImpl implements SonarqubeService {
    private DockerClientConfig dockerConfig;
    private DockerHttpClient dockerHttpClient;
    private DockerClient dockerClient;
    private String containerId;

    public static final String LOCAL_REPOSITORIES_PATH = "cloned";

    public SonarqubeServiceImpl() {
        dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(100)
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerConfig, dockerHttpClient);
    }

    public boolean isAvailable() {
        DockerHttpClient.Request request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path("/_ping")
                .build();

        try {
            DockerHttpClient.Response response = dockerHttpClient.execute(request);
            return (response.getStatusCode() == 200);
        }
        catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    public void createContainer(String projectName, String projectKey, String sonarqubeDirPath) throws IOException {

        File confDir = new File(sonarqubeDirPath);
        if (!confDir.exists()) {
            confDir.mkdir();
            System.out.println("Created directory " + confDir.getPath());
        }

        final Path dockerFilePath = Paths.get(Main.sonarqubeDir + File.separator + "Dockerfile");
        final Path copiedPath = Paths.get(sonarqubeDirPath + "Dockerfile");
        Files.copy(dockerFilePath, copiedPath, StandardCopyOption.REPLACE_EXISTING);

        File confFile = new File(sonarqubeDirPath + "sonar-project.properties");
        FileOutputStream confFileStream = new FileOutputStream(confFile, false);
        confFileStream.write(("sonar.projectName=" + projectName + "\n").getBytes(StandardCharsets.UTF_8));
        confFileStream.write(("sonar.projectKey=" + projectKey + "\n").getBytes(StandardCharsets.UTF_8));
        confFileStream.flush();
        confFileStream.close();
        System.out.println("Project configured");

        final String imageId = dockerClient.buildImageCmd(confDir).exec(new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                System.out.println("Item: " + item);
                super.onNext(item);
            }
        }).awaitImageId();
        System.out.println("Image ID: " + imageId);

        final HostConfig hostConfig = HostConfig.newHostConfig()
//                        .withBinds(Bind.parse(confDir + ":/opt/sonarqube/conf"))
                    .withPortBindings(PortBinding.parse("9000:9000"));
        System.out.println("Host configured");
        CreateContainerResponse response = dockerClient.createContainerCmd(imageId)//"sonarqube:latest")
            .withName("sonarqube")
            .withHostConfig(hostConfig)
            .exec();

        System.out.println("Container created");
        containerId = response.getId();
        System.out.println("Container ID: " + containerId);
    }

    public void startContainer() {
        dockerClient.startContainerCmd(containerId).exec();
    }

    public void stopContainer() {
        dockerClient.stopContainerCmd(containerId).exec();
    }
}
