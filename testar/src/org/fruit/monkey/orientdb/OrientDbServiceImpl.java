package org.fruit.monkey.orientdb;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Volume;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.fruit.monkey.docker.DockerPoolService;

import java.io.File;
import java.io.IOException;

public class OrientDbServiceImpl implements OrientDBService {

    private final String LOCAL_ORIENTDB_DIR = "db-init";
    private final DockerPoolService dockerPoolService;
    private final Settings settings;
    private OrientDBServiceDelegate delegate;


    public OrientDbServiceImpl(DockerPoolService dockerPoolService, Settings settings) {
        this.settings = settings;
        this.dockerPoolService = dockerPoolService;
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
    public void startLocalDatabase() throws IOException {
        dockerPoolService.start("orientdb");


        if (delegate != null)
            delegate.onStateChanged(OrientDBServiceDelegate.State.BUILDING_IMAGE, "Building the orientdb database image");
        final String imageId = dockerPoolService.buildImage(new File(Main.orientDBDir),
                "FROM orientdb:3.0.34\n" +
                        "ENV ORIENTDB_ROOT_PASSWORD=testar\n" +
                        "CMD sh ./db-init/init.sh & ./bin/server.sh\n"

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

        try {
            //TODO: Implement a better way to test the connection.
            Thread.sleep(15000);
        } catch (Exception ex) {
            // Ignore
        }


        if (delegate != null) {
            delegate.onServiceReady();
        }


    }
}
