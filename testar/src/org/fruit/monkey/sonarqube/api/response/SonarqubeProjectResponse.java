package org.fruit.monkey.sonarqube.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarqubeProjectResponse extends SonarqubeResponse {

    private List<Component> components;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {
        private String key;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
        private LocalDateTime lastAnalysisDate;
    }
}