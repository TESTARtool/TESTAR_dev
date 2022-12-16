package org.fruit.monkey.sonarqube.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.testar.JavaOutputParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarqubeProjectResponse {

    private Paging paging;
    private List<Component> components;

    public String getCurrentProjectKey() {
        var currentProject = components.stream()
                                                  .max(Comparator.comparing(t -> t.lastAnalysisDate))
                                                  .orElseThrow(SonarqubeApiException::projectNotFoundException);
        return currentProject.getKey();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {
        private String key;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
        private LocalDateTime lastAnalysisDate;
    }
}
