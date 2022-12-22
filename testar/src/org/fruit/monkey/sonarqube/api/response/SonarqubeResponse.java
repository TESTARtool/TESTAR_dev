package org.fruit.monkey.sonarqube.api.response;

import lombok.Data;

@Data
public abstract class SonarqubeResponse {
    private Paging paging;
}
