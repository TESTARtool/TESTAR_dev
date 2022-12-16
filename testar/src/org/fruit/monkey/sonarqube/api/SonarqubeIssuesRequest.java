package org.fruit.monkey.sonarqube.api;

public class SonarqubeIssuesRequest extends SonarqubeRequest<SonarqubeIssuesResponse> {

    private static final String REPORT_ENDPOINT_PATH = "/api/issues/search?";

    private static final String PROJECT_PARAM_KEY = "componentKeys";

    private static final String PAGE_PARAM_KEY = "p";

    private static final String PAGE_SIZE_PARAM_KEY = "ps";

    private final String searchParam;

    private final int pageIndex;

    private final int pageSize;

    public SonarqubeIssuesRequest(String host, String username, String searchParam, int pageIndex, int pageSize) {
        super(host, username, SonarqubeIssuesResponse.class);
        this.searchParam = searchParam;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public SonarqubeIssuesRequest(String host, String username, String searchParam) {
        this(host, username, searchParam, 1, 100);
    }

    @Override
    protected String buildUri() {
        return host + REPORT_ENDPOINT_PATH + PROJECT_PARAM_KEY + "=" + searchParam
                + "&" + PAGE_PARAM_KEY + "=" + pageIndex
                + "&" + PAGE_SIZE_PARAM_KEY + "=" + pageSize;
    }
}
