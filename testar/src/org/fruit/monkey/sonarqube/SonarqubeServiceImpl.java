package org.fruit.monkey.sonarqube;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.api.model.WaitResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolService;
import org.fruit.monkey.docker.DockerPoolServiceImpl;
import org.fruit.monkey.jacoco.JacocoReportParser;
import org.fruit.monkey.javaparser.JavaProjectParser;
import org.fruit.monkey.sonarqube.api.SonarqubeApiClient;
import org.fruit.monkey.sonarqube.model.SQComponent;
import org.fruit.monkey.sonarqube.model.SQPage;
import org.fruit.monkey.weights.FileAnalysedMethodEntryRepository;
import org.fruit.monkey.weights.StaticAnalysisResolver;
import org.testar.monkey.Main;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SonarqubeServiceImpl implements SonarqubeService {

    private final String serviceId;
    private HttpClient httpClient;
    private DockerPoolService dockerPoolService;
    private SonarqubeServiceDelegate delegate;

    private String token;

    private Gson gson = new Gson();

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
    public void analyseProject(String projectName, String projectKey, String sonarqubeDirPath, String projectSourceDir, String projectSubdir) throws TestarServiceException {
        if (!dockerPoolService.isDockerAvailable()) {
            throw new TestarServiceException(TestarServiceException.DOCKER_UNAVAILABLE);
        }

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
                final String serviceImageId = dockerPoolService.buildImage(confDir, "FROM sonarqube:latest\n");
                dockerPoolService.startWithImage(serviceImageId, "sonarqube", hostConfig, null,
                                                 DockerPoolService.StrategyIfPresent.REINSTALL);
            } catch (Exception e) {
                System.out.println("Cannot build Sonarqube image");
                e.printStackTrace();
                if (delegate != null) {
                    delegate.onError(SonarqubeServiceDelegate.ErrorCode.SERVICE_ERROR, e.getLocalizedMessage());
                }
                return;
            }

            // 2. Obtain a token as an admin

            if (delegate != null) {
                delegate.onStageChange(SonarqubeServiceDelegate.InfoStage.CREATING_SERVICE, "Service is ready to run");
                delegate.onStatusChange("Waiting for response", null, null);
            }

            String errorMessage = "Could not register on a local service";

            try {
                token = obtainServiceToken(projectKey, projectName);
            } catch (Exception e) {
                e.printStackTrace();
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
                scannerContainerId = createAndStartScanner(projectSourceDir, projectKey, projectName, projectSubdir, token);
                System.out.println("Scanner container ID: " + scannerContainerId);
            } catch (Exception e) {
                e.printStackTrace();
                delegate.onError(SonarqubeServiceDelegate.ErrorCode.CONNECTION_ERROR, e.getLocalizedMessage());
                return;
            }

            // 4. Waiting for a report

            System.out.println("Waiting for report");
            awaitingReport = true;
            if (delegate != null) {
                delegate.onStageChange(SonarqubeServiceDelegate.InfoStage.CREATING_SCANNER, "Scanning a project");
                delegate.onStatusChange("Please be patient", null, null);
            }
            dockerPoolService.getClient().waitContainerCmd(scannerContainerId).exec(new ResultCallback<WaitResponse>() {

                private int connectionTries = 1;
                private static final int MAX_CONNECTION_TRIES = 3;

                @Override
                public void onStart(Closeable closeable) {
                }

                @Override
                public void onNext(WaitResponse object) {
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("Exception has been produced when try no. " + connectionTries + " was taken to connect to SQ container.");
                    throwable.printStackTrace();
                    if (connectionTries <= MAX_CONNECTION_TRIES) {
                        ++connectionTries;
                        System.out.println("Retrying...");
                    } else {
                        System.out.println("Maximum number of SQ container connection tries has been reached");
                        if (delegate != null) {
                            delegate.onError(SonarqubeServiceDelegate.ErrorCode.ANALYSING_ERROR, throwable.getLocalizedMessage());
                        }
                        System.out.println("Closing containers");
                        dockerPoolService.dispose(false);
                    }
                }

                @Override
                public void onComplete() {
                    try {
                        final String issuesReport = obtainIssuesReport(projectKey);
                        System.out.println(issuesReport);
                        if (delegate != null) {
                            delegate.onComplete(issuesReport);
                        }
                        var jacocoPath = Paths.get(Main.sonarqubeOutputDir, "jacoco.xml").toString();
                        System.out.println("JACOCO: " + jacocoPath);
                        var staticAnalysisResolver = new StaticAnalysisResolver(
                                new SonarqubeApiClient("http://localhost:9000", token),
                                new JacocoReportParser(jacocoPath),
                                new JavaProjectParser(projectSourceDir)
                        );

                        var repository = new FileAnalysedMethodEntryRepository();
                        repository.saveAll(staticAnalysisResolver.resolveMethodEntries());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("No report available: " + e.getLocalizedMessage());
                        if (delegate != null) {
                            delegate.onError(SonarqubeServiceDelegate.ErrorCode.ANALYSING_ERROR, e.getLocalizedMessage());
                        }
                    } finally {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("-= Dispose on complete =-");
//                        dockerPoolService.dispose(false);
                    }
                }

                @Override
                public void close() throws IOException {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong");
            if (delegate != null) {
                delegate.onError(SonarqubeServiceDelegate.ErrorCode.UNKNOWN_ERROR, e.getLocalizedMessage());
            }
        } finally {
            if (!awaitingReport) {
                System.out.println("-= Not awaiting report =-");
//                dockerPoolService.dispose(false);
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
        ClassicHttpResponse newTokenResponse = null;

        while (status >= 400 && status < 500) {
            try {
                newTokenResponse = (ClassicHttpResponse) httpClient.execute(newTokenRequest);
                status = newTokenResponse.getCode();
            } catch (Exception e) {
                System.out.println("Not yet ready..."/* + e*/);
            } finally {
                if (status >= 400 && status < 500) {
                  newTokenRequest.reset();
                }
//                newTokenRequest.releaseConnection();
            }

            // if (status != HttpStatus.SC_OK) {
            //     System.out.println("Status: " + status);
            // }

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }

        if (status != HttpStatus.SC_OK) {
            System.out.println("Cannot create token: " + status);
            return null;
        }

        final String responseString;
        try {
            responseString = EntityUtils.toString(newTokenResponse.getEntity());
            System.out.println(responseString);
            return new JSONObject(responseString).getString("token");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
          newTokenRequest.reset();
        }

        return null;
    }

    private String createAndStartScanner(String sourcePath, String projectKey, String projectName, String projectSubdir, String token) throws IOException {
//        File projectFile = new File(sourcePath + "/" + projectSubdir + "/" + "sonar-project.properties");
//        System.out.println("Project file: " + projectFile.getAbsolutePath());
//        FileOutputStream projectStream = new FileOutputStream(projectFile);
//
//        projectStream.write(("sonar.projectKey=" + projectKey + "\n").getBytes(StandardCharsets.UTF_8));
//        projectStream.write(("sonar.projectName=" + projectName + "\n").getBytes(StandardCharsets.UTF_8));
//        projectStream.write(("sonar.sourceEncoding=UTF-8\n").getBytes(StandardCharsets.UTF_8));
//
//        projectStream.flush();
//        projectStream.close();

        final String outputDir = new File(Main.sonarqubeOutputDir).getAbsolutePath();
        final HostConfig hostConfig = HostConfig.newHostConfig()
                                                .withBinds(new Bind(outputDir, new Volume("/output")))
                                                .withCpuCount(4L);
        final String dockerfileContent =
                "FROM sonarsource/sonar-scanner-cli:latest AS sonarqube_scan\n" +
                        "COPY . /usr/src \n" +
                        "ENV SONAR_HOST_URL http://testar-sonarqube:9000\n" +
                        "ENV SONAR_TOKEN " + token + "\n" +
                        "ENV SRC_PATH /usr/src/" + projectSubdir + "\n" +
//                "ENV MAVEN_OPTS -javaagent:/usr/jacoco/org.jacoco.agent-0.8.8-runtime.jar=destfile=/usr/src/report/jacoco.exec,includes=*,jmx=true,dumponexit=true\n" +
//                "RUN mkdir /usr/jacoco\n" +
                        "RUN apk add maven openjdk11\n" +

//                "RUN wget https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.8/org.jacoco.agent-0.8.8-runtime.jar -P /usr/jacoco\n" +
//                "RUN mkdir /usr/src/report\n" +
                        "WORKDIR /usr/src/" + projectSubdir + "\n" +
                        //"RUN if [ -f \"./pom.xml\" ] || [ -f \"gradlew\" ]; then apk add maven openjdk11; fi\n" +
                        //"RUN apk add maven openjdk11\n" +
                        "CMD mvn org.jacoco:jacoco-maven-plugin:0.8.8:prepare-agent verify " +
                          "org.jacoco:jacoco-maven-plugin:0.8.2:report sonar:sonar -Dsonar.java.coveragePlugin=jacoco " +
                          "&& cp target/site/jacoco/jacoco.xml /output";// -D sonar.projectKey=yoho-be -D sonar.host.url=http://sonarqube:9000 -D sonar.login=" + token + ";";
        // "CMD if ! [ -f \"sonar-project.properties\"]; then printf \"sonar.projectKey=" + projectKey +
        //         "\\nsonar.projectName=" + projectName + "\\nsonar.sourceEncoding=UTF-8\" > " +
        //         "sonar-project.properties; fi; " +
        //         "if [ -f \"./pom.xml\" ]; then mvn clean verify sonar:sonar -D sonar.projectKey=yoho-be -D sonar.host.url=http://sonarqube:9000 -D sonar.login=" + token + ";" +
        //         "elif [ -f \"gradlew\" ]; then ./gradlew -Dsonar.host.url=$SONAR_HOST_URL sonarqube; " +
        //         "else sonar-scanner; " +
        //         "fi";
        //"RUN npm install typescript --save\n";

        final String imageId = dockerPoolService.buildImage(new File(sourcePath), dockerfileContent);
        final String containerId = dockerPoolService.startWithImage(imageId, "sonar-scanner", hostConfig, null,
                                                                    DockerPoolService.StrategyIfPresent.REINSTALL);

        return containerId;

    }

    private String obtainIssuesReport(String projectKey) throws IOException, JSONException {
        HttpGet issuesRequest = new HttpGet("http://localhost:9000/api/issues/search");
        issuesRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        int total = 0;
        int tries = 0;
        String report = null;
        while (total == 0/* && tries < 10*/) {
            System.out.println("Scanning in progress..."/* + tries*/);
            ClassicHttpResponse issuesResponse = (ClassicHttpResponse) httpClient.execute(issuesRequest);
            if (issuesResponse.getCode() == HttpStatus.SC_OK) {
                try {
                    report = EntityUtils.toString(issuesResponse.getEntity());
                    total = new JSONObject(report).getInt("total");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (total == 0) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                }

                tries++;
            }
            issuesRequest.reset();
        }
        return report;
    }

    public String obtainLastProjectKey() throws IOException {

        //TODO: obtain the last page if applicable

        HttpGet projectsRequest = new HttpGet("http://localhost:9000/api/projects/search");
        projectsRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        int total = 0;
        int tries = 0;
        String projectKey = null;
        while (total == 0 && tries < 10) {
            ClassicHttpResponse projectsResponse = (ClassicHttpResponse) httpClient.execute(projectsRequest);
            if (projectsResponse.getCode() == HttpStatus.SC_OK) {
                try {
                    Type projectsPageType = new TypeToken<SQPage<SQComponent>>() {
                    }.getType();
                    SQPage<SQComponent> projectsPage = gson.fromJson(EntityUtils.toString(projectsResponse.getEntity()), projectsPageType);
                    SQComponent[] components = projectsPage.getComponents();
                    if (components != null) {
                        total = components.length;
                        if (total > 0) {
                            projectKey = components[total - 1].getKey();
                        }
                    }

                    tries++;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                projectsRequest.reset();
            }
        }
        return projectKey;
    }

}
