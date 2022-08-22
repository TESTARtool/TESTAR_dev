package org.fruit.monkey.sonarqube.model;

import lombok.Data;

import java.util.Date;

@Data
public class SQComponent {
    private String organization;
    private String key;
    private String name;
    private String qualifier;
    private String visibility;
    private Date lastAnalysisDate;
}
