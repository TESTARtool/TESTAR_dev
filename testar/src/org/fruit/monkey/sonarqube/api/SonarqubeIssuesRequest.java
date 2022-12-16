package org.fruit.monkey.sonarqube.api;

public class SonarqubeIssuesRequest extends SonarqubeRequest<SonarqubeIssuesResponse> {

    private static final String REPORT_ENDPOINT_PATH = "/api/issues/search?";

    private static final String PROJECT_PARAM_KEY = "componentKeys";

    private final String searchParam;

    public SonarqubeIssuesRequest(String username, String host, String searchParam) {
        super(username, host, SonarqubeIssuesResponse.class);
        this.searchParam = searchParam;
    }

    @Override
    protected String buildUri() {
        return host + REPORT_ENDPOINT_PATH + PROJECT_PARAM_KEY + "=" + searchParam;
    }
}
