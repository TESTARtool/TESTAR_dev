package org.fruit.monkey.sonarqube.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarqubeIssuesResponse extends SonarqubeResponse{
    private List<Issue> issues;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Issue {
        private String key;
        private String rule;
        private String severity;
        private String component;
        private Long line;
        private String status;
        private String message;
        private String type;
    }
}