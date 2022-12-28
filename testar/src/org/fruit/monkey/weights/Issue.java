package org.fruit.monkey.weights;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Issue implements Serializable {
    private Long id;
    private String key;
    private String type;
    private String severity;
    private Long line;
}
