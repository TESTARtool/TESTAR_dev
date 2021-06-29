package org.fruit.monkey.webserver;

import com.github.dockerjava.api.model.*;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.fruit.monkey.Main;
import org.fruit.monkey.docker.DockerPoolService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReportingServiceImpl implements ReportingService {

    private DockerPoolService dockerPoolService;
    private String imageId;
    private final File webserverDir;
    private final File outputDir;
    private ReportingServiceDelegate delegate;

    public ReportingServiceImpl(DockerPoolService dockerPoolService) throws IOException {
        this.dockerPoolService = dockerPoolService;
        webserverDir = new File(Main.webserverDir);
        outputDir = new File(Main.outputDir);
    }

    public ReportingServiceDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ReportingServiceDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void start(int port, String dbHostname, int dbPort, String dbName, String dbUsername, String dbPassword) throws IOException {
        if (delegate != null) {
            delegate.onStateChanged(ReportingServiceDelegate.State.BUILDING_IMAGE, "Building report service image");
        }
        imageId = dockerPoolService.buildImage(webserverDir, null);

        if (delegate != null) {
            delegate.onStateChanged(ReportingServiceDelegate.State.STARTING_SERVICE, "Starting report service");
        }
        final HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse(String.format("%d:80", port)))
                .withBinds(
                        new Bind(webserverDir.getAbsolutePath(), new Volume("/app")),
                        new Bind(outputDir.getAbsolutePath(), new Volume("/static/output"))
                );
        final String[] env = {"ADAPTER=MYSQL", "MYSQL_HOST=" + dbHostname, "MYSQL_PORT=" + dbPort,
            "MYSQL_DATABASE=" + dbName,  "MYSQL_USER=" + dbUsername, "MYSQL_PASSWORD=" + dbPassword,
            "MYSQL_WAIT=5"};

        dockerPoolService.startWithImage(imageId, "webservice", hostConfig, env);

        if (delegate != null) {
            delegate.onStateChanged(ReportingServiceDelegate.State.CONNECTING, "Connecting to report service");
        }

        final HttpClient httpClient = HttpClientBuilder.create().build();
        final String url = "http://localhost:" + port;
        final HttpGet testRequest = new HttpGet(url);
        HttpResponse testResponse;
        int status = 400;

        while (status >= 400 && status < 600) {
            boolean isError = false;
            try {
                testResponse = httpClient.execute(testRequest);
                status = testResponse.getStatusLine().getStatusCode();
            } catch (Exception e) {
                System.out.println("Not yet ready: " + e);
                isError = true;
            } finally {
                testRequest.releaseConnection();
            }

            if (!isError && status != HttpStatus.SC_OK) {
                System.out.println("Status: " + status);
            }

            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {}
        }
        delegate.onServiceReady(url);
    }
}
