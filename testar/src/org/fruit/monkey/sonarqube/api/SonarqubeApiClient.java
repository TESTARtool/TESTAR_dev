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
        var receivedProjectsAmount = projectsResponse.getPaging().getPageSize();
        var receivedProjects = projectsResponse.getComponents();
        if(projectsAmount > receivedProjectsAmount) {
            //TODO
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
