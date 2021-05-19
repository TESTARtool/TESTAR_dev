package org.fruit.monkey.sonarqube;

import org.codehaus.jettison.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface SonarqubeService {
    boolean isAvailable();
    void createContainer(String projectName, String projectKey, String sonarqubeDirPath)  throws IOException;
    void createClientContainer(String confPath, String sourcePath);
}
