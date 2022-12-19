package org.fruit.monkey.btrace;

import org.fruit.monkey.sonarqube.api.SonarqubeApiException;

public class BtraceApiException extends RuntimeException {

    private BtraceApiException(String message) {
        super(message);
    }

    private BtraceApiException(String message, Throwable e) {
        super(message, e);
    }

    public static BtraceApiException btraceApiCallResultedInError(Throwable e) {
        return new BtraceApiException("Sending request to Sonarqube API resulted in error", e);
    }
}
