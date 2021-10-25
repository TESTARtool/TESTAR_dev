package org.fruit.monkey.docker;

public interface DockerPoolServiceDelegate {
    void onStatusChange(String statusDescripton, Long currentPorgress, Long totalProgress);
}
