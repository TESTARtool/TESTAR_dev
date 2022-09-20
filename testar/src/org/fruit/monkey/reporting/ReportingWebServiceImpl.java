package org.fruit.monkey.reporting;

import com.github.dockerjava.api.model.*;
import org.fruit.monkey.ServiceUtil;
import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolService;
import org.fruit.monkey.docker.DockerPoolServiceDelegate;
import org.fruit.monkey.webserver.ReportingService;
import org.fruit.monkey.webserver.ReportingServiceDelegate;
import org.testar.monkey.Main;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class ReportingWebServiceImpl implements ReportingService {

  private ReportingServiceDelegate delegate;
  private DockerPoolService dockerPoolService;
  private String apiImageId;
  private String appImageId;
//  private String serviceId;

  private int apiPort;
  private int appPort;
  private String dbHost;
  private int dbPort;
  private String dbName;
  private String dbUser;
  private String dbPassword;


  @Override
  public ReportingServiceDelegate getDelegate() {
    return delegate;
  }

  @Override
  public void setDelegate(ReportingServiceDelegate delegate) {
    this.delegate = delegate;
  }

  public ReportingWebServiceImpl(DockerPoolService dockerPoolService, int apiPort, int appPort, String dbHost, int dbPort,
      String dbName, String dbUser, String dbPassword) {
    this.dockerPoolService = dockerPoolService;
    this.apiPort = apiPort;
    this.appPort = appPort;
    this.dbHost = dbHost;
    this.dbPort = dbPort;
    this.dbName = dbName;
    this.dbUser = dbUser;
    this.dbPassword = dbPassword;
  }

  @Override
  public void start(/*int apiPort, int appPort, String dbHost, long dbPort, String dbName, String dbUser, String dbPassword*/) throws TestarServiceException {
    if (!dockerPoolService.isDockerAvailable()) {
      throw new TestarServiceException(TestarServiceException.DOCKER_UNAVAILABLE);
    }

    String networkId = dockerPoolService.getNetworkId();
    if (networkId == null) {
      throw new TestarServiceException(TestarServiceException.POOL_SERVICE_UNAVAILABLE);
    }

    System.out.println("Network ID:" + networkId);

//    dockerPoolService.start(serviceId);

    File reportingApiDir = new File(Main.reportingApiDir);
    File reportingAppDir = new File(Main.reportingAppDir);
    File reportingTmpDir = new File(Main.reportingTmpDir);
    final HostConfig apiHostConfig = HostConfig.newHostConfig()
      .withPortBindings(PortBinding.parse(String.format("%d:8080", apiPort)));
    final HostConfig appHostConfig = HostConfig.newHostConfig()
      .withPortBindings(PortBinding.parse(String.format("%d:80", appPort)))
      .withBinds(
        new Bind(reportingTmpDir.getAbsolutePath(), new Volume("/tmp"))
//        new Bind(reportingApiDir.getAbsolutePath(), new Volume("/usr/src"))//,
//        new Bind(reportingAppDir.getAbsolutePath(), new Volume("/usr/app/static/output"))
      );
    try {
      apiImageId = dockerPoolService.buildImage(reportingApiDir, null);
      final String[] apiEnvironment = {"DB_HOST=" + dbHost, "DB_PORT=" + dbPort, "DB_NAME=" + dbName,
        "DB_USERNAME=" + dbUser, "DB_PASSWORD=" + dbPassword};
      dockerPoolService.startWithImage(apiImageId, "reporting-api", apiHostConfig, apiEnvironment);

      ServiceUtil.waitForConnection(String.format("http://localhost:%d/api/swagger-ui/index.html", apiPort));

      appImageId = dockerPoolService.buildImage(reportingAppDir, null);
      final String[] appEnvironment = {"API_SERVER=http://reporting_api"};
      dockerPoolService.startWithImage(appImageId, "reporting-app", appHostConfig, appEnvironment);

      String reportUrl = String.format("http://localhost:%d", appPort);
      ServiceUtil.waitForConnection(reportUrl);
      System.out.println("Report is ready");
      if (delegate != null) {
        delegate.onServiceReady(reportUrl);
      }
      else {
        System.out.println("No delegate available");
      }
    }
    catch (IOException e) {
      System.err.println("Could not create a reporting backend: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void stop() {
//    dockerPoolService.dispose(false);
  }
}
