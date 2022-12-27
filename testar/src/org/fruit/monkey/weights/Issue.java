package org.fruit.monkey.weights;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Issue {
    private Long id;
    private String key;
    private String type;
    private String severity;
    private Long line;
}
