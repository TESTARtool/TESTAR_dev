package org.fruit.monkey.sonarqube.api;

public class SonarqubeProjectRequest extends SonarqubeRequest<SonarqubeProjectResponse>{

    private static final String PROJECTS_ENDPOINT_PATH = "/api/projects/search?";

    private static final String PAGE_PARAM_KEY = "p";

    private static final String PAGE_SIZE_PARAM_KEY = "ps";

    private final int pageIndex;

    private final int pageSize;


    public SonarqubeProjectRequest(String host, String username, int pageIndex, int pageSize) {
        super(host, username, SonarqubeProjectResponse.class);
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public SonarqubeProjectRequest(String host, String username) {
        this(host, username, 1, 100);
    }

    @Override
    protected String buildUri() {
        return host + PROJECTS_ENDPOINT_PATH + PAGE_PARAM_KEY + "=" + pageIndex
                + "&" + PAGE_SIZE_PARAM_KEY + "=" + pageSize;
    }
}
