package org.fruit.monkey.javaparser;

public class JavaParserException extends RuntimeException{

    public JavaParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public static JavaParserException projectParsingProducedException(Throwable cause) {
        return new JavaParserException("Exception produced by JavaParser when tried to parse java project", cause);
    }
}
