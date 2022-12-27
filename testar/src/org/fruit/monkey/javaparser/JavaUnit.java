package org.fruit.monkey.javaparser;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JavaUnit {
    private String unitName;
    private String fileLocation;
    private List<MethodDeclaration> methods;
}
