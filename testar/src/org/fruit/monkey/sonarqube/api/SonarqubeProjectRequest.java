package org.fruit.monkey.sonarqube.api;

public class SonarqubeProjectRequest extends SonarqubeRequest<SonarqubeProjectResponse>{

    private static final String PROJECTS_ENDPOINT_PATH = "/api/projects/search?";

    private static final String PAGE_PARAM_KEY = "p";

    private final int pageIndex;


    public SonarqubeProjectRequest(String host, String username, int pageIndex) {
        super(host, username, SonarqubeProjectResponse.class);
        this.pageIndex = pageIndex;
    }

    public SonarqubeProjectRequest(String host, String username) {
        this(host, username, 1);
    }

    @Override
    protected String buildUri() {
        return host + PROJECTS_ENDPOINT_PATH + PAGE_PARAM_KEY + "=" + pageIndex;
    }
}
