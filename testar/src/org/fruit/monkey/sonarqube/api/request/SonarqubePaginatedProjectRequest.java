package org.fruit.monkey.sonarqube.api.request;

import org.fruit.monkey.sonarqube.api.response.SonarqubeProjectResponse;

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
