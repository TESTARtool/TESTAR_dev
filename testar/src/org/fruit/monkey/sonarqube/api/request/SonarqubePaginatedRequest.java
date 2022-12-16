package org.fruit.monkey.sonarqube.api.request;

import org.fruit.monkey.sonarqube.api.SonarqubeApiException;
import org.fruit.monkey.sonarqube.api.response.SonarqubeResponse;

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
        var sonarqubePageRequest = initialPageRequest();
        var sonarqubePageResponse = sonarqubePageRequest.send();

        total = sonarqubePageResponse.getPaging().getTotal();
        pageSize = sonarqubePageResponse.getPaging().getPageSize();
        currentPage = sonarqubePageResponse.getPaging().getPageIndex();

        return sonarqubePageResponse;
    }

    private S fetchNextPage() {
        var sonarqubePageRequest = pageRequest(++currentPage);
        return sonarqubePageRequest.send();
    }

    protected int getPageSize() {
        return pageSize;
    }
}
