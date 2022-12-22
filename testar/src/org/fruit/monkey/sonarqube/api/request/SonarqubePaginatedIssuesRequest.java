package org.fruit.monkey.sonarqube.api.request;

import org.fruit.monkey.sonarqube.api.response.SonarqubeIssuesResponse;

public class SonarqubePaginatedIssuesRequest extends SonarqubePaginatedRequest<SonarqubeIssuesRequest, SonarqubeIssuesResponse>{

    private final String project;

    public SonarqubePaginatedIssuesRequest(String host, String token, String project) {
        super(host, token);
        this.project = project;
    }

    @Override
    SonarqubeIssuesRequest initialPageRequest() {
        return new SonarqubeIssuesRequest(host, token, project);
    }

    @Override
    SonarqubeIssuesRequest pageRequest(int pageIndex) {
        return new SonarqubeIssuesRequest(host, token, project, pageIndex, getPageSize());
    }
}
