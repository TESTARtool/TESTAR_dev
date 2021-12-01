package org.fruit.monkey.sonarqube;

import org.fruit.monkey.docker.DockerPoolServiceDelegate;

public interface AdvancedDockerPoolServiceDelegate extends DockerPoolServiceDelegate {
    enum InfoStage {
        CREATING_SERVICE, CONNECTING_SERVICE, CREATING_SCANNER, ANALYSING_PROJECT, OBTAINING_REPORT
    }
    enum ErrorCode {
        SERVICE_ERROR, CONNECTION_ERROR, SCANNER_ERROR, ANALYSING_ERROR, REPORT_ERROR, UNKNOWN_ERROR
    }

    void onStageChange(InfoStage stage, String description);
    void onError(ErrorCode errorCode, String message);
    void onComplete(String report);
}
