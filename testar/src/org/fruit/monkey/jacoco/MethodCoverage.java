package org.fruit.monkey.jacoco;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class MethodCoverage {
    private String className;
    private String methodName;
    private int linesCovered;
    private int linesMissed;
}
