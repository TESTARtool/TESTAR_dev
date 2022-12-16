package org.fruit.monkey.sonarqube.api;

import lombok.RequiredArgsConstructor;
import org.fruit.monkey.sonarqube.api.request.SonarqubePaginatedProjectRequest;
import org.fruit.monkey.sonarqube.api.response.SonarqubeProjectResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

}
