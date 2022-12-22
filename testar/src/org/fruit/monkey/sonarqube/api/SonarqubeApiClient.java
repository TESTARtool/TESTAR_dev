package org.fruit.monkey.sonarqube.api;

import lombok.RequiredArgsConstructor;
import org.fruit.monkey.sonarqube.api.request.SonarqubePaginatedIssuesRequest;
import org.fruit.monkey.sonarqube.api.request.SonarqubePaginatedProjectRequest;
import org.fruit.monkey.sonarqube.api.response.SonarqubeIssuesResponse;
import org.fruit.monkey.sonarqube.api.response.SonarqubeProjectResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SonarqubeApiClient {

    private final String host;

    private final String token;

    public String getProjectComponentKey() {
        var projectsRequest = new SonarqubePaginatedProjectRequest(host, token);
        var receivedProjects = new ArrayList<SonarqubeProjectResponse.Component>();

        while(projectsRequest.hasNextPage()) {
            receivedProjects.addAll(projectsRequest.getNextPage().getComponents());
        }

        return getCurrentProjectKey(receivedProjects);
    }

    private String getCurrentProjectKey(List<SonarqubeProjectResponse.Component> components) {
        var currentProject = components.stream()
                .max(Comparator.comparing(SonarqubeProjectResponse.Component::getLastAnalysisDate))
                .orElseThrow(SonarqubeApiException::projectNotFoundException);
        return currentProject.getKey();
    }

    public List<SQIssue> getDetectedIssues(String project) {
        var issuesRequest = new SonarqubePaginatedIssuesRequest(host, token, project);
        var receivedIssues = new ArrayList<SonarqubeIssuesResponse.Issue>();

        while(issuesRequest.hasNextPage()) {
            receivedIssues.addAll(issuesRequest.getNextPage().getIssues());
        }

        return receivedIssues.stream().map(SQIssue::of).collect(Collectors.toList());
    }

}
