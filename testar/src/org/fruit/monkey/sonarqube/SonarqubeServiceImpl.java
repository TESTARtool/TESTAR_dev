package org.fruit.monkey.sonarqube;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateNetworkResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarqubeServiceImpl implements SonarqubeService {
    private DockerClientConfig dockerConfig;
    private DockerHttpClient dockerHttpClient;
    private DockerClient dockerClient;
    private String containerId;

    public static final String LOCAL_REPOSITORIES_PATH = "cloned";

    private HttpClient httpClient = HttpClientBuilder.create()/*.setDefaultCredentialsProvider(credentialsProvider)*/.build();
    private final static String authHeader = "Basic YWRtaW46YWRtaW4=";// admin:admin

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

    public void analyseProject(String projectName, String projectKey, String sonarqubeDirPath, String projectSourceDir) throws IOException {

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
        CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageId)
            .withName("sonarqube")
            .withHostName("sonarqube")
            .withHostConfig(hostConfig)
            .exec();

        System.out.println("Container created");
        containerId = containerResponse.getId();
        System.out.println("Container ID: " + containerId);

        CreateNetworkResponse networkResponse = dockerClient.createNetworkCmd().withName("sonar").withDriver("bridge").withAttachable(true).exec();
        String networkId = networkResponse.getId();
        dockerClient.connectToNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();

        dockerClient.startContainerCmd(containerId).exec();
        String token = null;
        try {
            token = createProjectAndGetToken(projectKey, projectName, "testar");
            System.out.println("Sonarqube scan token: " + token);
        }
        catch (Exception e) {
            System.err.println("Something went wrong: " + e.getLocalizedMessage());
        }

        if (token == null) {
            //TODO: throw exception
            return;
        }
        final String clientId = createClientContainer(networkId, projectSourceDir, projectKey, token);
        dockerClient.waitContainerCmd(clientId).exec(new ResultCallback<WaitResponse>() {
            @Override
            public void onStart(Closeable closeable) {

            }

            @Override
            public void onNext(WaitResponse object) {

            }

            @Override
            public void onError(Throwable throwable) {
                //TODO: throe exception
            }

            @Override
            public void onComplete() {
                try {
                    System.out.println(obtainIssuesReport(projectKey));
                }
                catch (Exception e) {
                    System.out.println("No report available: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void close() throws IOException {
                System.out.println("Closing containers");
            }
        });
    }

    public void stopContainer() {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    private String createProjectAndGetToken(String projectKey, String projectName, String tokenName) throws IOException, JSONException {
        int status = HttpStatus.SC_NOT_FOUND;

        final HttpPost newProjectRequest = new HttpPost("http://localhost:9000/api/projects/create");

        List<NameValuePair> form = new ArrayList<>();

//        form.add(new BasicNameValuePair("project", projectKey));
//        form.add(new BasicNameValuePair("name", projectName));
//
//        newProjectRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
//        newProjectRequest.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
//
//        System.out.println("Creating new project...");
//        status = HttpStatus.SC_NOT_FOUND;
//        HttpResponse newProjectResponse = null;
//        while (status >= 400 && status < 500) {
//            try {
//                newProjectResponse = httpClient.execute(newProjectRequest);
//                status = newProjectResponse.getStatusLine().getStatusCode();
//                if (status != HttpStatus.SC_OK) {
//                    System.out.println("Still " + status);
//                }
//            }
//            catch (Exception e) {
//                System.out.println("Not yet ready");
//            }
//            finally {
//                newProjectRequest.releaseConnection();
//            }
//
//            try {
//                Thread.sleep(5000);
//            }
//            catch (Exception e) {}
//        }
//        System.out.println("Finished with status " + status);
//
//        if (status != HttpStatus.SC_OK) {
//            System.out.println("Cannot create project: " + newProjectResponse.getStatusLine().getStatusCode());
//            return null;
//        }
//        System.out.println("...done");
//
//        form.clear();

        form.add(new BasicNameValuePair("name", tokenName));
        final HttpPost newTokenRequest = new HttpPost("http://localhost:9000/api/user_tokens/generate");
        newTokenRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        newTokenRequest.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        HttpResponse newTokenResponse = null;

        while (status >= 400 && status < 500) {
            try {
                newTokenResponse = httpClient.execute(newTokenRequest);
                status = newTokenResponse.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    System.out.println("Still " + status);
                }
            } catch (Exception e) {
                System.out.println("Not yet ready");
            } finally {
                newTokenRequest.releaseConnection();
            }

            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {}
        }

        System.out.println("Token receiving status: " + status);
        if (status != HttpStatus.SC_OK) {
            System.out.println("Cannot create token: " + status);
            return null;
        }

        System.out.println("Creating new token");
        return new JSONObject(EntityUtils.toString(newTokenResponse.getEntity())).getString("token");
    }

    private String createClientContainer(String networkId, String sourcePath, String projectKey, String token) throws IOException {

        File configFile = new File(sourcePath + "sonar-scanner.properties");
        FileOutputStream configStream = new FileOutputStream(configFile);
        configStream.write(("sonar.projectBaseDir=/usr/src\n").getBytes(StandardCharsets.UTF_8));
        configStream.write(("sonar.sources=/usr/src\n").getBytes(StandardCharsets.UTF_8));
        configStream.flush();
        configStream.close();

        File projectFile = new File(sourcePath + "sonar-project.properties");
        FileOutputStream projectStream = new FileOutputStream(projectFile);

        projectStream.write(("sonar.projectKey=" + projectKey + "\n").getBytes(StandardCharsets.UTF_8));
        projectStream.write(("sonar.projectName=Demo\n").getBytes(StandardCharsets.UTF_8));
        projectStream.write(("sonar.sourceEncoding=UTF-8\n").getBytes(StandardCharsets.UTF_8));
//        projectStream.write(("sonar.sources=\"sonarqube-scanner-gradle/gradle-basic\"\n").getBytes(StandardCharsets.UTF_8));

        projectStream.flush();
        projectStream.close();

        File dockerFile = new File(sourcePath + "Dockerfile");
        FileOutputStream dockerStream = new FileOutputStream(dockerFile);

//        dockerStream.write("FROM ubuntu AS sonarqube_scan\n".getBytes(StandardCharsets.UTF_8));
//        dockerStream.write("FROM lequal/sonar-scanner AS sonarqube_scan\n".getBytes(StandardCharsets.UTF_8));
//        dockerStream.write("WORKDIR /app\n".getBytes(StandardCharsets.UTF_8));
//        dockerStream.write("COPY . .\n".getBytes(StandardCharsets.UTF_8));
//        dockerStream.write("RUN chmod -R a+w /app\n".getBytes(StandardCharsets.UTF_8));
////        dockerStream.write("RUN apt-get update && apt-get install -y iputils-ping\n".getBytes(StandardCharsets.UTF_8));
////        dockerStream.write("CMD ping sonarqube\n".getBytes(StandardCharsets.UTF_8));
////        dockerStream.write(("CMD sonar-scanner" +
////                " -Dsonar.host.url=\"http://sonarqube:9000\"" +
////                " -Dsonar.projectKey=\"" + projectKey + "\"" +
////                " -Dsonar.sources=\".\"\n").getBytes(StandardCharsets.UTF_8));
//
//        dockerStream.write(("CMD cd /app/sonarqube-scanner-gradle/gradle-basic && ./gradlew -Dsonar.host.url=http://sonarqube:9000 sonarqube").getBytes(StandardCharsets.UTF_8));

        dockerStream.write("FROM sonarsource/sonar-scanner-cli AS sonarqube_scan\n".getBytes(StandardCharsets.UTF_8));
//        dockerStream.write("COPY sonar-scanner.properties /opt/sonar-scanner/conf/sonar-scanner.properties\n".getBytes(StandardCharsets.UTF_8));
        dockerStream.write("ENV SONAR_HOST_URL http://sonarqube:9000\n".getBytes(StandardCharsets.UTF_8));
        dockerStream.write(("ENV SONAR_TOKEN " + token  + "\n").getBytes(StandardCharsets.UTF_8));
//        dockerStream.write("CMD ping sonarqube".getBytes(StandardCharsets.UTF_8));

        dockerStream.flush();
        dockerStream.close();

        String imageId = dockerClient.buildImageCmd(new File(sourcePath)).exec(new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                System.out.println("Item: " + item);
                super.onNext(item);
            }
        }).awaitImageId();

//        Map<String, String> sourceVolumeMap = new HashMap<>();
//        sourceVolumeMap.put("sonarqube-scanner-gradle/gradle-basic", "/usr/src");
//        Volume sourceVolume = Volume.parse(sourceVolumeMap);
        Volume sourceVolume = Volume.parse(".:/usr/src");
        CreateContainerResponse response = dockerClient.createContainerCmd(imageId)
                .withName("sonar-scanner")
                .withHostName("scanner")
//                .withEnv("SONAR_HOST_URL=http://sonarqube:9000")
//                .withEnv("SONAR_LOGIN=" + token)
//                .withVolumes(sourceVolume)
                .withBinds(new Bind(sourcePath, new Volume("/usr/src")))
                .exec();

        containerId = response.getId();
        dockerClient.connectToNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();
        dockerClient.startContainerCmd(containerId).exec();

        Network network = dockerClient.inspectNetworkCmd().withNetworkId(networkId).exec();
        System.out.println(network.toString());

        return containerId;
    }

    private String obtainIssuesReport(String projectKey) throws IOException, JSONException {
        HttpGet issuesRequest = new HttpGet("http://localhost:9000/api/issues/search");
        issuesRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        int total = 0;
        int tries = 0;
        String report = null;
        while (total == 0 && tries < 10) {
            System.out.println("Try " + tries);
            HttpResponse issuesResponse = httpClient.execute(issuesRequest);
            if (issuesResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                System.out.println("Report obtained");
                report = EntityUtils.toString(issuesResponse.getEntity());
                total = new JSONObject(report).getInt("total");
            }
            if (total == 0) {
                try {
                    Thread.sleep(5000);
                }
                catch (Exception e) {}

                tries++;
            }
            issuesRequest.releaseConnection();
        }
        return report;
    }
}
