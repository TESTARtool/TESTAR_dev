package org.fruit.monkey.sonarqube.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SonarqubeApiClientTest extends SonarqubeApiTest {

    @Test
    public void shouldReturnProjectKey() throws IOException {
        var apiClient = new SonarqubeApiClient(host, "username");

        mockResponse("sonarqube-api/projects/sonarqube-multiple-projects-response.json");

        var projectComponentKey = apiClient.getProjectComponentKey();

        assertEquals("com.marviq.yoho:yoho-be-api", projectComponentKey);
    }

    @Test
    public void shouldReturnProjectKeyWhenMultiplePagesAvailable() throws IOException {
        var apiClient = new SonarqubeApiClient(host, "username");

        mockResponse("sonarqube-api/projects/sonarqube-multiple-projects-response-multiple-page-1.json");
        mockResponse("sonarqube-api/projects/sonarqube-multiple-projects-response-multiple-page-2.json");
        mockResponse("sonarqube-api/projects/sonarqube-multiple-projects-response-multiple-page-3.json");
        mockResponse("sonarqube-api/projects/sonarqube-multiple-projects-response-multiple-page-4.json");

        var projectComponentKey = apiClient.getProjectComponentKey();

        assertEquals("com.marviq.yoho:yoho-be-api", projectComponentKey);
    }

    @Test
    public void shouldReturnIssue() throws IOException {
        var apiClient = new SonarqubeApiClient(host, "username");

        mockResponse("sonarqube-api/issues/sonarqube-issues-response.json");

        var issues = apiClient.getDetectedIssues("com.marviq.yoho:yoho-be-api");
        var detectedIssue = issues.get(0);

        assertEquals(1, issues.size());
        assertEquals("AYURC_GPHq_OHAqW8iRJ", detectedIssue.getKey());
        assertEquals("java:S110", detectedIssue.getRule());
        assertEquals("MAJOR", detectedIssue.getSeverity());
        assertEquals("src/main/java/com/marviq/yoho/app/service/factory/exception/FactoryNotFoundException.java",
                     detectedIssue.getLocation());
        assertEquals(8L, detectedIssue.getLine().longValue());
        assertEquals("OPEN", detectedIssue.getStatus());
        assertEquals("This class has 10 parents which is greater than 5 authorized.", detectedIssue.getMessage());
        assertEquals("CODE_SMELL", detectedIssue.getType());
    }

    @Test
    public void shouldReturnIssuesWhenMultiplePagesAvailable() throws IOException {
        var apiClient = new SonarqubeApiClient(host, "username");

        mockResponse("sonarqube-api/issues/sonarqube-multiple-issues-response-multiple-page-1.json");
        mockResponse("sonarqube-api/issues/sonarqube-multiple-issues-response-multiple-page-2.json");
        mockResponse("sonarqube-api/issues/sonarqube-multiple-issues-response-multiple-page-3.json");
        mockResponse("sonarqube-api/issues/sonarqube-multiple-issues-response-multiple-page-4.json");

        var detectedIssues = apiClient.getDetectedIssues("com.marviq.yoho:yoho-be-api");

        assertEquals(35, detectedIssues.size());
    }
}