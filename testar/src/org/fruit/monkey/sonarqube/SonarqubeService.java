package org.fruit.monkey.sonarqube;

import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;

public interface SonarqubeService {
    DockerPoolService getDockerService();
    void analyseProject(String projectName, String projectKey, String sonarqubeDirPath,
                        String projectSourceDir, SonarqubeServiceDelegate delegate)  throws IOException;
}
