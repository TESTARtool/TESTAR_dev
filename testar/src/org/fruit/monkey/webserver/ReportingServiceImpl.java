package org.fruit.monkey.webserver;

import com.github.dockerjava.api.model.*;
import org.fruit.monkey.Main;
import org.fruit.monkey.docker.DockerPoolService;

import java.io.File;
import java.io.IOException;

public class ReportingServiceImpl implements ReportingService {

    private DockerPoolService dockerPoolService;
    private final String imageId;
    private final File webserverDir;
    private final File outputDir;

    public ReportingServiceImpl(DockerPoolService dockerPoolService) throws IOException {
        this.dockerPoolService = dockerPoolService;
        webserverDir = new File(Main.webserverDir);
        outputDir = new File(Main.outputDir);
        imageId = dockerPoolService.buildImage(webserverDir, null);
    }

    @Override
    public void start(int port, String dbHostname, int dbPort, String dbName, String dbUsername, String dbPassword) throws IOException {
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
    }
}
