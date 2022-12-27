package org.fruit.monkey.weights;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class AnalysedMethodEntry {
    private Long id;
    private String className;
    private String methodName;
    private List<String> parameters;
    private BigDecimal coverage;
    private List<Issue> issues;
}
