package org.fruit.monkey.sonarqube.api;

import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class SonarqubeApiClient {

    private final String host;

    private final String token;

    public String getProjectComponentKey() {
        var projectsRequest = new SonarqubeProjectRequest(host, token);
        var projectsResponse = projectsRequest.send();
        var projectsAmount = projectsResponse.getPaging().getTotal();
        var receivedProjectsAmount = projectsResponse.getComponents().size();
        var receivedProjects = projectsResponse.getComponents();
        var pageSize = projectsResponse.getPaging().getPageSize();
        var page = projectsResponse.getPaging().getPageIndex();

        while(receivedProjectsAmount < projectsAmount) {
            projectsRequest = new SonarqubeProjectRequest(host, token, ++page, pageSize);
            projectsResponse = projectsRequest.send();
            receivedProjects.addAll(projectsResponse.getComponents());
            receivedProjectsAmount += projectsResponse.getComponents().size();
        }
        return getCurrentProjectKey(receivedProjects);
    }

    private String getCurrentProjectKey(List<SonarqubeProjectResponse.Component> components) {
        var currentProject = components.stream()
                .max(Comparator.comparing(SonarqubeProjectResponse.Component::getLastAnalysisDate))
                .orElseThrow(SonarqubeApiException::projectNotFoundException);
        return currentProject.getKey();
    }

}
