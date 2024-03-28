package org.fruit.monkey.javaparser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor // Generate constructor with required fields
public class JavaUnit {
    private final String unitName;
    private final String fileLocation;
    private final List<MethodDeclaration> methods;
    private int startLine = 0;
    private int endLine = 0;
}
