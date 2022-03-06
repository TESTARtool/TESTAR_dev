package org.fruit.monkey.webserver;

import com.github.dockerjava.api.model.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.fruit.monkey.docker.DockerPoolService;
import org.testar.monkey.Main;

import java.io.File;
import java.io.IOException;

public class ReportingServiceImpl implements ReportingService {

    private DockerPoolService dockerPoolService;
    private String imageId;
    private final File webserverDir;
    private final int port;
    private final String[] environmentVariables;
    private final File outputDir;
    private boolean orientdb = false;
    private ReportingServiceDelegate delegate;

    public ReportingServiceImpl(int port, String[] env, DockerPoolService dockerPoolService) throws IOException {
        this.dockerPoolService = dockerPoolService;
        this.port = port;
        this.environmentVariables = env;

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
    public void start() throws IOException {
        dockerPoolService.start("reporting");
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
                        new Bind(webserverDir.getAbsolutePath(), new Volume("/usr/app")),
                        new Bind(outputDir.getAbsolutePath(), new Volume("/usr/app/static/output"))
                );

        dockerPoolService.startWithImage(imageId, "webservice", hostConfig, environmentVariables);

        if (delegate != null) {
            delegate.onStateChanged(ReportingServiceDelegate.State.CONNECTING, "Connecting to report service");
        }

        final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        final String url = "http://localhost:" + port;
        final HttpGet testRequest = new HttpGet(url);
        HttpResponse testResponse;
        int status = 400;

        while (status >= 400 && status < 600) {
            boolean isError = false;
            try {
                testResponse = httpClient.execute(testRequest);
                status = testResponse.getCode();
            } catch (Exception e) {
                System.out.println("Not yet ready: " + e);
                isError = true;
//            } finally {
//                testRequest.releaseConnection();
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
