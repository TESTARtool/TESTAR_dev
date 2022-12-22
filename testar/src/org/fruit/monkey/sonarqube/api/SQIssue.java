package org.fruit.monkey.sonarqube.api;

import lombok.Getter;
import org.fruit.monkey.sonarqube.api.response.SonarqubeIssuesResponse;

@Getter
public class SQIssue {

    private final String key;
    private final String rule;
    private final String severity;
    private final String location;
    private final Long line;
    private final String status;
    private final String message;
    private final String type;

    private SQIssue(String key, String rule, String severity, String location, Long line, String status, String message,
                   String type) {
        this.key = key;
        this.rule = rule;
        this.severity = severity;
        this.location = location;
        this.line = line;
        this.status = status;
        this.message = message;
        this.type = type;
    }

    public static SQIssue of(SonarqubeIssuesResponse.Issue issue) {
        return new SQIssue(issue.getKey(), issue.getRule(), issue.getSeverity(), extractLocation(issue.getComponent()),
                           issue.getLine(), issue.getStatus(), issue.getMessage(), issue.getType());
    }

    private static String extractLocation(String component) {
        var splitComponent = component.split(":");
        return splitComponent[splitComponent.length-1];
    }
}
