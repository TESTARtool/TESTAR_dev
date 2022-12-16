package org.fruit.monkey.sonarqube.api;

import lombok.Data;

@Data
public class Paging {
    private int pageIndex;
    private int pageSize;
    private int total;

}
