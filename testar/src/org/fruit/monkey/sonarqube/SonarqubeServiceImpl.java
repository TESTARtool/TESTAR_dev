package org.fruit.monkey.sonarqube;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.net.util.Base64;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
                    .withPortBindings(PortBinding.parse("9000:9000"));
        System.out.println("Host configured");
        CreateContainerResponse response = dockerClient.createContainerCmd(imageId)
            .withName("sonarqube")
            .withHostConfig(hostConfig)
            .exec();

        System.out.println("Container created");
        containerId = response.getId();
        System.out.println("Container ID: " + containerId);

        dockerClient.startContainerCmd(containerId).exec();
        try {
            final String token = createProjectAndGetToken(projectKey, projectName, "testar");
            System.out.println("Sonarqube scan token: " + token);
        }
        catch (Exception e) {
            System.err.println("Something went wrong: " + e.getLocalizedMessage());
        }
    }

    public void stopContainer() {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    public String createProjectAndGetToken(String projectKey, String projectName, String tokenName) throws IOException, JSONException {

        HttpClient httpClient = HttpClientBuilder.create()/*.setDefaultCredentialsProvider(credentialsProvider)*/.build();

        int status = 0;

        final HttpPost newProjectRequest = new HttpPost("http://localhost:9000/api/projects/create");
        final String authHeader = "Basic YWRtaW46YWRtaW4=";// admin:admin

        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("project", projectKey));
        form.add(new BasicNameValuePair("name", projectName));

        newProjectRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        newProjectRequest.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));

        System.out.println("Creating new project...");
        status = HttpStatus.SC_NOT_FOUND;
        HttpResponse newProjectResponse = null;
        while (status >= 400 && status < 500) {
            try {
                newProjectResponse = httpClient.execute(newProjectRequest);
                status = newProjectResponse.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    System.out.println("Still " + status);
                }
            }
            catch (Exception e) {
                System.out.println("Not yet ready");
            }
            finally {
                newProjectRequest.releaseConnection();
            }

            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {}
        }
        System.out.println("Finished with status " + status);

        if (status != HttpStatus.SC_OK) {
            System.out.println("Cannot create project: " + newProjectResponse.getStatusLine().getStatusCode());
            return null;
        }
        System.out.println("...done");

        form.clear();
        System.out.println("(0)");
        form.add(new BasicNameValuePair("name", tokenName));
        System.out.println("(1)");
        final HttpPost newTokenRequest = new HttpPost("http://localhost:9000/api/user_tokens/generate");
        System.out.println("(2)");
        newTokenRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        System.out.println("(3)");
        newTokenRequest.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        System.out.println("(4)");

        HttpResponse newTokenResponse = httpClient.execute(newTokenRequest);
        status = newTokenResponse.getStatusLine().getStatusCode();

        newTokenRequest.releaseConnection();

        System.out.println("Token receiving status: " + status);
        if (status != HttpStatus.SC_OK) {
            System.out.println("Cannot create token: " + status);
            return null;
        }

        System.out.println("Creating new token");
        return new JSONObject(EntityUtils.toString(newTokenResponse.getEntity())).getString("token");
    }

    public void createClientContainer(String confPath, String sourcePath) {
        //TODO
    }
}
