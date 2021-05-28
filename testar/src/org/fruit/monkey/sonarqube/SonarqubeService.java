package org.fruit.monkey.sonarqube;

import java.io.IOException;

public interface SonarqubeService {
    void analyseProject(String projectName, String projectKey, String sonarqubeDirPath,
                        String projectSourceDir, SonarqubeServiceDelegate delegate)  throws IOException;
}
