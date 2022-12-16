package org.fruit.monkey.sonarqube.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SonarqubeApiClientTest extends SonarqubeApiTest {

    @Test
    public void shouldReturnProjectKey() throws IOException {
        var apiClient = new SonarqubeApiClient(host, "username");

        mockResponse("sonarqubeapi/sonarqube-multiple-projects-response.json");

        var projectComponentKey = apiClient.getProjectComponentKey();

        assertEquals("com.marviq.yoho:yoho-be-api", projectComponentKey);
    }

}