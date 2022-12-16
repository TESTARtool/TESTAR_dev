package org.fruit.monkey.sonarqube.api;

public class SonarqubePaginatedProjectRequest {

    private final String host;
    private final String token;

    private int currentPage = 0;

    private int pageSize;

    private int total;

    public SonarqubePaginatedProjectRequest(String host, String token) {
        this.host = host;
        this.token = token;
    }

    public boolean hasNextPage() {
        return currentPage==0 || pageSize*currentPage < total;
    }

    public SonarqubeProjectResponse getNextPage() {
        if(!hasNextPage()) {
            throw SonarqubeApiException.noMorePages();
        }

        if(currentPage==0) {
            return fetchInitialPage();
        } else {
            return fetchNextPage();
        }

    }

    private SonarqubeProjectResponse fetchInitialPage() {
        var projectsRequest = new SonarqubeProjectRequest(host, token);
        var projectsResponse = projectsRequest.send();

        total = projectsResponse.getPaging().getTotal();
        pageSize = projectsResponse.getPaging().getPageSize();
        currentPage = projectsResponse.getPaging().getPageIndex();

        return projectsResponse;
    }

    private SonarqubeProjectResponse fetchNextPage() {
        var projectsRequest = new SonarqubeProjectRequest(host, token, ++currentPage, pageSize);
        return projectsRequest.send();
    }
}
