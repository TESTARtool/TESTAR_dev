package org.fruit.monkey.sonarqube;

import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;

public interface SonarqubeService {
    AdvancedDockerPoolServiceDelegate getDelegate();
    void setDelegate(AdvancedDockerPoolServiceDelegate delegate);
    DockerPoolService getDockerService();
    void analyseProject(String projectName, String projectKey, String sonarqubeDirPath,
                        String projectSourceDir)  throws IOException;
}
