package org.fruit.monkey.sonarqube.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SonarqubeApiClientTest extends SonarqubeApiTest {

    @Test
    public void shouldReturnProjectKey() throws IOException {
        var apiClient = new SonarqubeApiClient(host, "username");

        mockResponse("sonarqubeapi/projects/sonarqube-multiple-projects-response.json");

        var projectComponentKey = apiClient.getProjectComponentKey();

        assertEquals("com.marviq.yoho:yoho-be-api", projectComponentKey);
    }

    @Test
    public void shouldReturnProjectKeyWhenMultiplePagesAvailable() throws IOException {
        var apiClient = new SonarqubeApiClient(host, "username");

        mockResponse("sonarqubeapi/projects/sonarqube-multiple-projects-response-multiple-page-1.json");
        mockResponse("sonarqubeapi/projects/sonarqube-multiple-projects-response-multiple-page-2.json");
        mockResponse("sonarqubeapi/projects/sonarqube-multiple-projects-response-multiple-page-3.json");
        mockResponse("sonarqubeapi/projects/sonarqube-multiple-projects-response-multiple-page-4.json");

        var projectComponentKey = apiClient.getProjectComponentKey();

        assertEquals("com.marviq.yoho:yoho-be-api", projectComponentKey);
    }

}