package org.fruit.monkey.sonarqube.api;

public class SonarqubePaginatedProjectRequest extends SonarqubePaginatedRequest<SonarqubeProjectRequest, SonarqubeProjectResponse>{

    public SonarqubePaginatedProjectRequest(String host, String token) {
        super(host, token);
    }

    @Override
    SonarqubeProjectRequest initialPageRequest() {
        return new SonarqubeProjectRequest(host, token);
    }

    @Override
    SonarqubeProjectRequest pageRequest(int pageIndex) {
        return new SonarqubeProjectRequest(host, token, pageIndex, getPageSize());
    }
}
