package org.fruit.monkey.sonarqube.api;

public class SonarqubeApiException extends RuntimeException {
    private SonarqubeApiException(String message) {
        super(message);
    }

    private SonarqubeApiException(String message, Throwable e) {
        super(message, e);
    }

    public static SonarqubeApiException projectNotFoundException() {
        return new SonarqubeApiException("No project found in Sonarqube.");
    }

    public static SonarqubeApiException sonarqubeApiCallResultedInError(Throwable e) {
        return new SonarqubeApiException("Sending request to Sonarqube API resulted in error", e);
    }

    public static SonarqubeApiException noMorePages() {
        return new SonarqubeApiException("No more pages of paginated request.");
    }
}
