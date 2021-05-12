package org.fruit.monkey.sonarqube;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

public class SonarqubeServiceImpl implements SonarqubeService {
    private DockerClientConfig dockerConfig;
    private DockerHttpClient dockerClient;

    public SonarqubeServiceImpl() {
        dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(100)
                .build();
    }

    public boolean isAvailable() {
        DockerHttpClient.Request request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path("/_ping")
                .build();

        try {
            DockerHttpClient.Response response = dockerClient.execute(request);
            return (response.getStatusCode() == 200);
        }
        catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }
}
