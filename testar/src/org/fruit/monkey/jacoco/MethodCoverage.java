package org.fruit.monkey.jacoco;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class MethodCoverage {
    private String className;
    private String methodName;
    private int initLine;
    private int linesCovered;
    private int linesMissed;
}
