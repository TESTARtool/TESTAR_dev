package org.fruit.monkey.weights;

public class FileRepositoryException extends RuntimeException {
    private FileRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FileRepositoryException repositoryFileNotFound(String filePath, Throwable e) {
        return new FileRepositoryException(String.format("Repository file %s not found", filePath), e);
    }

    public static FileRepositoryException repositoryFileDeserializationIssue(String filePath, Throwable e) {
        return new FileRepositoryException(String.format("Repository file %s could not be deserialized", filePath), e);
    }

    public static FileRepositoryException repositoryFileSerializationIssue(String filePath, Throwable e) {
        return new FileRepositoryException(String.format("Repository file %s could not be serialized", filePath), e);
    }
}
