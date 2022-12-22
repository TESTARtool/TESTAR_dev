package org.fruit.monkey.sonarqube.api.response;

import lombok.Data;

@Data
public class Paging {
    private int pageIndex;
    private int pageSize;
    private int total;

}
