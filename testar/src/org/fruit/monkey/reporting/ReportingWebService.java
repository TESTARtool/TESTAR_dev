package org.fruit.monkey.reporting;

import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolServiceDelegate;

import java.net.URI;

public interface ReportingWebService {
  DockerPoolServiceDelegate getDelegate();
  void setDelegate(DockerPoolServiceDelegate delegate);

  void start(int apiPort, int appPort, String dbHost, long dbPort, String dbName, String dbUser, String dbPassword) throws TestarServiceException;
  void stop();
}
