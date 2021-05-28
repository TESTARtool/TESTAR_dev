package org.fruit.monkey.sonarqube;

public interface SonarqubeServiceDelegate {
    enum InfoStatus {
        CREATING_SERVICE, CONNECTING_SERVICE, CREATING_SCANNER, ANALYSING_PROJECT, OBTAINING_REPORT
    }
    enum ErrorCode {
        SERVICE_ERROR, CONNECTION_ERROR, SCANNER_ERROR, ANALYSING_ERROR, REPORT_ERROR, UNKNOWN_ERROR
    }

    void onInfoMessage(InfoStatus status, String message);
    void onError(ErrorCode errorCode, String message);
    void onComplete(String report);
}
