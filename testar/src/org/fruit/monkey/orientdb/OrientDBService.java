package org.fruit.monkey.orientdb;

import org.fruit.monkey.docker.DockerPoolService;

import java.io.IOException;

public interface OrientDBService {

    DockerPoolService getDockerPoolService();

    OrientDBServiceDelegate getDelegate();

    void setDelegate(OrientDBServiceDelegate delegate);


    void startLocalDatabase(String database, String username, String password) throws IOException;

}
