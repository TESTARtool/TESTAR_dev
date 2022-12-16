package org.fruit.monkey.sonarqube.api;

public class SonarqubeIssuesRequest extends SonarqubeRequest<SonarqubeIssuesResponse> {

    private static final String REPORT_ENDPOINT_PATH = "/api/issues/search?";

    private static final String PROJECT_PARAM_KEY = "componentKeys";

    private static final String PAGE_PARAM_KEY = "p";

    private final String searchParam;

    private final int pageIndex;

    public SonarqubeIssuesRequest(String username, String host, String searchParam, int pageIndex) {
        super(username, host, SonarqubeIssuesResponse.class);
        this.searchParam = searchParam;
        this.pageIndex = pageIndex;
    }

    public SonarqubeIssuesRequest(String username, String host, String searchParam) {
        this(username, host, searchParam, 1);
    }

    @Override
    protected String buildUri() {
        return host + REPORT_ENDPOINT_PATH + PROJECT_PARAM_KEY + "=" + searchParam +
                "&" + PAGE_PARAM_KEY + "=" + pageIndex;
    }
}
