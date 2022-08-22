package org.fruit.monkey.sonarqube.model;

import lombok.Data;

@Data
public class SQPage<T> {
    private int pageIndex;
    private int pageSize;
    private int total;

    private T[] components;
}
