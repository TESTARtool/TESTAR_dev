package org.fruit.monkey.sonarqube;

import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolService;
import org.fruit.monkey.mysql.MySqlServiceDelegate;

import java.io.IOException;

public interface SonarqubeService {
    SonarqubeServiceDelegate getDelegate();
    void setDelegate(SonarqubeServiceDelegate delegate);
    DockerPoolService getDockerService();
    void analyseProject(String projectName, String projectKey, String sonarqubeDirPath,
                        String projectSourceDir, String projectSubdir)  throws IOException, TestarServiceException;
    String obtainLastProjectKey() throws IOException;
}
