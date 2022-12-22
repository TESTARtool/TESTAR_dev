package org.fruit.monkey.javaparser;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MethodDeclaration {
    private String name;
    private List<String> parameters;
    private int startLine;
    private int endLine;
}
