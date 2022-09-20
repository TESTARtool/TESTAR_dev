package org.fruit.monkey;

public class TestarServiceException extends Exception {

  public final static String DOCKER_UNAVAILABLE = "Cannot find Docker instance. Please make sure Docker is up and running on your machine";
  public final static String POOL_SERVICE_UNAVAILABLE = "Docker pool service cannot be connected. Please make sure it's up and running";

  public TestarServiceException(String message) {
        super(message);
    }
}
