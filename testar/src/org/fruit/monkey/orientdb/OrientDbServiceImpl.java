package org.fruit.monkey.orientdb;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Volume;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.fruit.monkey.docker.DockerPoolService;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OrientDbServiceImpl implements OrientDBService {

    private final String LOCAL_ORIENTDB_DIR = "db-init";
    private final DockerPoolService dockerPoolService;
    private final Settings settings;
    private OrientDBServiceDelegate delegate;
    private HttpClient httpClient;

    public OrientDbServiceImpl(DockerPoolService dockerPoolService, Settings settings) {
        this.settings = settings;
        this.dockerPoolService = dockerPoolService;
        httpClient = HttpClientBuilder.create().build();
    }


    @Override
    public OrientDBServiceDelegate getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(OrientDBServiceDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public DockerPoolService getDockerPoolService() {
        return dockerPoolService;
    }

    @Override
    public void startLocalDatabase(String database, String username, String password) throws IOException {
        System.out.println("Database: " + database);
        System.out.println("User: " + username);
        System.out.println("Password: " + password);
        dockerPoolService.start("reporting");


        if (delegate != null)
            delegate.onStateChanged(OrientDBServiceDelegate.State.BUILDING_IMAGE, "Building the orientdb database image");
        final String imageId = dockerPoolService.buildImage(new File(Main.orientDBDir),
                "FROM orientdb:3.0.34\n" +
                        "ENV ORIENTDB_DATABASE="+database+"\n" +
                        "ENV ORIENTDB_USERNAME="+username+"\n" +
                        "ENV ORIENTDB_PASSWORD="+password+"\n" +
                        "CMD sh ./db-init/init.sh\n"

        );
        if (delegate != null) {
            delegate.onStateChanged(OrientDBServiceDelegate.State.STARTING_SERVICE, "Starting orientdb database");
        }

        final File databaseDir = new File(settings.get(ConfigTags.DataStoreDirectory) + File.separator + "orientdb");
        if (databaseDir.isDirectory()) {
            System.out.println("Directory exists: " + databaseDir.getAbsolutePath());
        } else {
            System.out.println("Directory does not exist: " + databaseDir.getAbsolutePath());
        }
        final File databaseInitDir = new File(Main.orientDBDir + File.separator + LOCAL_ORIENTDB_DIR);
        if (databaseInitDir.isDirectory()) {
            System.out.println("Directory exists: " + databaseInitDir.getAbsolutePath());
        } else {
            System.out.println("Directory does not exist: " + databaseInitDir.getAbsolutePath());
        }
        final HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse("2424:2424"), PortBinding.parse("2480:2480"))
                .withBinds(
                        new Bind(databaseInitDir.getAbsolutePath(), new Volume("/orientdb/db-init")),
                        new Bind(databaseDir.getAbsolutePath(), new Volume("/orientdb/databases"))
                );
        dockerPoolService.startWithImage(imageId, "orientdb", hostConfig);

//        try {
//            System.out.println();
//            //TODO: Implement a better way to test the connection.
//            Thread.sleep(15000);
//        } catch (Exception ex) {
//            // Ignore
//        }

//        URL url = new URL("http://localhost:2480/connect/" + database);
//        String credentials = Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8);
//
//        URLConnection connection = url.openConnection();
//        connection.

        int status = HttpStatus.SC_NOT_FOUND;

        final HttpGet checkRequest = new HttpGet("http://localhost:2480/connect/" + database);
        checkRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " +
                Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8)));
        HttpResponse checkResponse = null;
        while (status >= 400 && status < 500) {
            try {
                checkResponse = httpClient.execute(checkRequest);
                status = checkResponse.getCode();
            } catch (Exception e) {
                System.out.println("Not yet ready: " + e);
//            } finally {
//                checkRequest.releaseConnection();
            }

            if (status != HttpStatus.SC_OK && status != HttpStatus.SC_NO_CONTENT) {
                System.out.println("Status: " + status);
            }
            try {
                Thread.sleep(5000);
            }
            catch (Exception e) {}
        }

        if (status != HttpStatus.SC_OK && status != HttpStatus.SC_NO_CONTENT) {
            System.out.println("Failed to initialize OrientDB: " + status);
        }

        if (delegate != null) {
            delegate.onServiceReady();
        }


    }
}
