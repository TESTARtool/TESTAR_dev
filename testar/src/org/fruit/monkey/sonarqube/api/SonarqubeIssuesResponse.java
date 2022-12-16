package org.fruit.monkey.sonarqube.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
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
//      private   "com.marviq.yoho:yoho-be-api:src/main/java/com/marviq/yoho/app/service/factory/exception/FactoryNotFoundException.java"
        private Long line;
        private String status;
        private String message;
        private String type;
    }
}
