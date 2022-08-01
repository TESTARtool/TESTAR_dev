package org.fruit.monkey;

public class TestarServiceException extends Exception {

    public final static String DOCKER_UNAVAILABLE = "Cannot find Docker instance. Please make sure Docker is up and running on your machine";

    public TestarServiceException(String message) {
        super(message);
    }
}
