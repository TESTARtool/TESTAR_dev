package org.fruit.monkey.sonarqube;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface SonarqubeService {
    boolean isAvailable();
    void createContainer(String projectName, String projectKey, String sonarqubeDirPath)  throws IOException;
    void startContainer();
    void stopContainer();
}
