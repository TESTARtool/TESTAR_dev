package org.fruit.monkey.weights;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class WeightVerdict {
    private BigDecimal severityVerdict;
    private List<AnalysedMethodEntry> methodsIncluded;

    public static WeightVerdict zero() {
        return new WeightVerdict(BigDecimal.ZERO, new ArrayList<>());
    }
}
