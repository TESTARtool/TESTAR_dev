package org.fruit.monkey.sonarqube.api;

import lombok.Data;

@Data
public abstract class SonarqubeResponse {
    private Paging paging;
}
