package org.fruit.monkey.sonarqube.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.fruit.monkey.sonarqube.api.response.SonarqubeIssuesResponse;

@Getter
@AllArgsConstructor
public class SQIssue {

    private final String key;
    private final String rule;
    private final String severity;
    private final String location;
    private final Long line;
    private final String status;
    private final String message;
    private final String type;
    public static SQIssue of(SonarqubeIssuesResponse.Issue issue) {
        return new SQIssue(issue.getKey(), issue.getRule(), issue.getSeverity(), extractLocation(issue.getComponent()),
                           issue.getLine(), issue.getStatus(), issue.getMessage(), issue.getType());
    }

    private static String extractLocation(String component) {
        var splitComponent = component.split(":");
        return splitComponent[splitComponent.length-1];
    }
}
