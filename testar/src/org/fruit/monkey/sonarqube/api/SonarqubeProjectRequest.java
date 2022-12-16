package org.fruit.monkey.sonarqube.api;

public class SonarqubeProjectRequest extends SonarqubeRequest<SonarqubeProjectResponse>{

    private static final String PROJECTS_ENDPOINT_PATH = "/api/projects/search";


    public SonarqubeProjectRequest(String host, String username) {
        super(host, username, SonarqubeProjectResponse.class);
    }

    @Override
    protected String buildUri() {
        return host + PROJECTS_ENDPOINT_PATH;
    }
}
