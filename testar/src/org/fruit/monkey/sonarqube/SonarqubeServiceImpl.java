package org.fruit.monkey.sonarqube;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.*;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.fruit.monkey.docker.DockerPoolService;
import org.fruit.monkey.docker.DockerPoolServiceDelegate;
import org.fruit.monkey.docker.DockerPoolServiceImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SonarqubeServiceImpl implements SonarqubeService {

    private final String serviceId;
    private HttpClient httpClient ;
    private DockerPoolService dockerPoolService;
    private SonarqubeServiceDelegate delegate;

    private final static String authHeader = "Basic YWRtaW46YWRtaW4=";// admin:admin

    public SonarqubeServiceImpl(String serviceId) {
        this.serviceId = serviceId;
        this.dockerPoolService = new DockerPoolServiceImpl();
        httpClient = HttpClientBuilder.create().build();
    }

    public SonarqubeServiceDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(SonarqubeServiceDelegate delegate) {
        this.delegate = delegate;
        dockerPoolService.setDelegate(delegate);
    }

    public DockerPoolService getDockerService() {
        return dockerPoolService;
    }

    @Override
    public void analyseProject(String projectName, String projectKey, String sonarqubeDirPath, String projectSourceDir) {
        System.out.println("Analysing a project " + projectName);
        dockerPoolService.start(serviceId);
        boolean awaitingReport = false;
        try {
            // 1. Create and start a service

            if (delegate != null) {
                delegate.onStageChange(SonarqubeServiceDelegate.InfoStage.CREATING_SERVICE, "Creating a service");
            }

            File confDir = new File(sonarqubeDirPath);
            if (!confDir.exists()) {
                confDir.mkdir();
            }

            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withPortBindings(PortBinding.parse("9000:9000"));

            try {
                final String serviceImageId = dockerPoolService.buildImage(confDir, "FROM sonarqube:8.2-community\n");
                dockerPoolService.startWithImage(serviceImageId, "sonarqube", hostConfig);
            }
            catch (Exception e) {
                if (delegate != null) {
                    delegate.onError(SonarqubeServiceDelegate.ErrorCode.SERVICE_ERROR, e.getLocalizedMessage());
                }
                return;
            }

            // 2. Obtain a token as an admin

            if (delegate != null) {
                delegate.onStageChange(SonarqubeServiceDelegate.InfoStage.CREATING_SERVICE, "Waiting for service to start");
            }

            String token = null;
            String errorMessage = "Could not register on a local service";

            try {
                token = obtainServiceToken(projectKey, projectName);
            }
            catch (Exception e) {
                errorMessage = e.getLocalizedMessage();
            }

            if (token == null) {
                System.out.println("Token is null");
                if (delegate != null) {
                    delegate.onError(SonarqubeServiceDelegate.ErrorCode.CONNECTION_ERROR, errorMessage);
                }
                return;
            }

            // 3. Build and run a project scanner instance

            String scannerContainerId = null;

            if (delegate != null) {
                delegate.onStageChange(SonarqubeServiceDelegate.InfoStage.CREATING_SCANNER, "Building a project scanner instance");
            }

            try {
                scannerContainerId = createAndStartScanner(projectSourceDir, projectKey, projectName, token);
            } catch (Exception e) {
                delegate.onError(SonarqubeServiceDelegate.ErrorCode.CONNECTION_ERROR, e.getLocalizedMessage());
                return;
            }

            // 4. Waiting for a report

            awaitingReport = true;
            if (delegate != null) {
                delegate.onStageChange(SonarqubeServiceDelegate.InfoStage.CREATING_SCANNER, "Obtaining a scan report");
            }
            dockerPoolService.getClient().waitContainerCmd(scannerContainerId).exec(new ResultCallback<WaitResponse>() {

                @Override
                public void onStart(Closeable closeable) {
                }

                @Override
                public void onNext(WaitResponse object) {
                }

                @Override
                public void onError(Throwable throwable) {
                    if (delegate != null) {
                        delegate.onError(SonarqubeServiceDelegate.ErrorCode.ANALYSING_ERROR, throwable.getLocalizedMessage());
                    }
                    dockerPoolService.dispose(false);
                }

                @Override
                public void onComplete() {
                    try {
                        final String issuesReport = obtainIssuesReport(projectKey);
                        System.out.println(issuesReport);
                        if (delegate != null) {
                            delegate.onComplete(issuesReport);
                        }
                    }
                    catch (Exception e) {
                        System.out.println("No report available: " + e.getLocalizedMessage());
                        if (delegate != null) {
                            delegate.onError(SonarqubeServiceDelegate.ErrorCode.ANALYSING_ERROR, e.getLocalizedMessage());
                        }
                    }
                    finally {
                        dockerPoolService.dispose(false);
                    }
                }

                @Override
                public void close() throws IOException {
                }
            });

        } catch (Exception e) {
            System.out.println("Something went wrong");
            if (delegate != null) {
                delegate.onError(SonarqubeServiceDelegate.ErrorCode.UNKNOWN_ERROR, e.getLocalizedMessage());
            }
        }
        finally {
            if (!awaitingReport) {
                dockerPoolService.dispose(false);
            }
        }
    }

    private String obtainServiceToken(String projectKey, String projectName) throws IOException, JSONException {
        int status = HttpStatus.SC_NOT_FOUND;

        List<NameValuePair> form = new ArrayList<>();

        form.add(new BasicNameValuePair("name", "testar"));
        final HttpPost newTokenRequest = new HttpPost("http://localhost:9000/api/user_tokens/generate");
        newTokenRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        newTokenRequest.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        HttpResponse newTokenResponse = null;

        while (status >= 400 && status < 500) {
            try {
                newTokenResponse = httpClient.execute(newTokenRequest);
                status = newTokenResponse.getStatusLine().getStatusCode();
            } catch (Exception e) {
                System.out.println("Not yet ready: " + e);
            } finally {
                newTokenRequest.releaseConnection();
            }

            if (status != HttpStatus.SC_OK) {
                System.out.println("Status: " + status);
            }

            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {}
        }

        if (status != HttpStatus.SC_OK) {
            System.out.println("Cannot create token: " + status);
            return null;
        }

        final String responseString = EntityUtils.toString(newTokenResponse.getEntity());
        System.out.println(responseString);

        return new JSONObject(responseString).getString("token");
    }

    private String createAndStartScanner(String sourcePath, String projectKey, String projectName, String token) throws IOException {
        File projectFile = new File(sourcePath + "sonar-project.properties");
        FileOutputStream projectStream = new FileOutputStream(projectFile);

        projectStream.write(("sonar.projectKey=" + projectKey + "\n").getBytes(StandardCharsets.UTF_8));
        projectStream.write(("sonar.projectName=" + projectName + "\n").getBytes(StandardCharsets.UTF_8));
        projectStream.write(("sonar.sourceEncoding=UTF-8\n").getBytes(StandardCharsets.UTF_8));

        projectStream.flush();
        projectStream.close();

        final HostConfig hostConfig = HostConfig.newHostConfig()
                .withBinds(new Bind(sourcePath, new Volume("/usr/src")));
        final String dockerfileContent =
                "FROM sonarsource/sonar-scanner-cli:latest AS sonarqube_scan\n" +
                "ENV SONAR_HOST_URL http://sonarqube:9000\n" +
                "ENV SONAR_TOKEN " + token  + "\n";

        final String imageId = dockerPoolService.buildImage(new File(sourcePath), dockerfileContent);
        final String containerId = dockerPoolService.startWithImage(imageId, "sonar-scanner", hostConfig);

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
