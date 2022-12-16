package org.fruit.monkey.sonarqube.api;

public abstract class SonarqubePaginatedRequest<T extends SonarqubeRequest<S>, S extends SonarqubeResponse> {

    protected final String host;
    protected final String token;

    private int currentPage = 0;
    private int pageSize;

    private int total;

    public SonarqubePaginatedRequest(String host, String token) {
        this.host = host;
        this.token = token;
    }

    abstract T initialPageRequest();

    abstract T pageRequest(int pageIndex);

    public boolean hasNextPage() {
        return currentPage==0 || pageSize*currentPage < total;
    }

    public S getNextPage() {
        if(!hasNextPage()) {
            throw SonarqubeApiException.noMorePages();
        }

        if(currentPage==0) {
            return fetchInitialPage();
        } else {
            return fetchNextPage();
        }

    }

    private S fetchInitialPage() {
        var projectsRequest = initialPageRequest();
        var projectsResponse = projectsRequest.send();

        total = projectsResponse.getPaging().getTotal();
        pageSize = projectsResponse.getPaging().getPageSize();
        currentPage = projectsResponse.getPaging().getPageIndex();

        return projectsResponse;
    }

    private S fetchNextPage() {
        var projectsRequest = pageRequest(++currentPage);
        return projectsRequest.send();
    }

    protected int getPageSize() {
        return pageSize;
    }
}
