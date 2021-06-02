package org.fruit.monkey.mysql;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Volume;
import org.fruit.monkey.Main;
import org.fruit.monkey.docker.DockerPoolService;
import org.fruit.monkey.docker.DockerPoolServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MySqlServiceImpl implements MySqlService {

    public static final String LOCAL_DATABASE_PATH = "bin" + File.separator + "db";
    public static final String LOCAL_DATABASE_INIT_PATH = "db-init";

    private DockerPoolService dockerPoolService;

    public MySqlServiceImpl() throws IOException {
        dockerPoolService = new DockerPoolServiceImpl("mysql");
        final String imageId = dockerPoolService.buildImage(new File(Main.databaseDir), null);
        final String databasePath = Paths.get(System.getProperty("user.dir"))
                .getParent().resolve(LOCAL_DATABASE_PATH).toString();
        final String databaseInitPath = Main.extrasDir + File.separator + LOCAL_DATABASE_INIT_PATH;
        final HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse("13306:3306"))
                .withBinds(
                        new Bind(databasePath, new Volume("/var/lib/mysql")),
                        new Bind(databaseInitPath, new Volume("/docker-entrypoint-initdb.d"))
                    );
        dockerPoolService.startWithImage(imageId, "mysql", hostConfig);
    }

    public DockerPoolService getDockerPoolService() {
        return dockerPoolService;
    }
}
