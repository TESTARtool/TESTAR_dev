package org.fruit.monkey.javaparser;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.utils.SourceRoot;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JavaProjectParser {

    private final String sourcePath;
    private List<JavaUnit> javaUnits;

    public List<JavaUnit> parseJavaUnits() {
        javaUnits = new ArrayList<>();
        try {
            Path pathToSource = Path.of(sourcePath);
            SourceRoot sourceRoot = new SourceRoot(pathToSource);
            sourceRoot.tryToParse();
            List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();
            compilations.forEach(this::parseCompilationUnit);
        } catch (IOException e) {
            throw JavaParserException.projectParsingProducedException(e);
        }
        return javaUnits;
    }

    private void parseCompilationUnit(CompilationUnit compilationUnit) {
        for(TypeDeclaration<?> typeDeclaration: compilationUnit.getTypes()) {
            if(typeDeclaration.isClassOrInterfaceDeclaration()) {
                parseClass(typeDeclaration.asClassOrInterfaceDeclaration());
            } else if (typeDeclaration.isEnumDeclaration()) {
                parseClass(typeDeclaration.asEnumDeclaration());
            }
        }
    }

    private void parseClass(TypeDeclaration<?> declaration) {
        var javaUnit = new JavaUnit(declaration.getFullyQualifiedName().orElse(""),
                                   declaration.getMethods().stream()
                                                           .map(this::parseMethod)
                                                           .collect(Collectors.toList()));

        declaration.getMembers().forEach(member -> {
            if(member.isClassOrInterfaceDeclaration()) {
                parseClass(member.asClassOrInterfaceDeclaration());
            } else if (member.isConstructorDeclaration()) {
                javaUnit.getMethods().add(parseMethod(member.asConstructorDeclaration()));
            }
        });
        javaUnits.add(javaUnit);
    }

    private MethodDeclaration parseMethod(CallableDeclaration<?> methodDeclaration) {
        var methodRange = methodDeclaration.getRange().orElse(new Range(new Position(0,0), new Position(0,0)));
        var parameters = methodDeclaration.getParameters().stream().map(param -> param.getType().toString()).collect(Collectors.toList());
        return new MethodDeclaration(methodDeclaration.isConstructorDeclaration() ? "<init>" : methodDeclaration.getNameAsString(),
                                     parameters,
                                     methodRange.begin.line,
                                     methodRange.end.line);
    }
}
