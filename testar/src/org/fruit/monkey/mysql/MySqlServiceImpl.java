package org.fruit.monkey.mysql;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Volume;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.docker.DockerPoolService;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.Settings;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class MySqlServiceImpl implements MySqlService {

    private static final String LOCAL_DATABASE_PATH = "bin" + File.separator + "db";
    private static final String LOCAL_DATABASE_INIT_PATH = "db-init";
    private static final String LAST_ID_QUERY = "SELECT LAST_INSERT_ID()";

    private DockerPoolService dockerPoolService;
    private Settings settings;
    private Connection connection;
    private PreparedStatement lastIdStatement;

    private MySqlServiceDelegate delegate;

    public MySqlServiceImpl(DockerPoolService dockerPoolService, Settings settings) {
        this.settings = settings;
        this.dockerPoolService = dockerPoolService;
    }

    public MySqlServiceDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(MySqlServiceDelegate delegate) {
        this.delegate = delegate;
    }

    public synchronized void startLocalDatabase(String databaseName, String userName, String userPassword) throws IOException, ClassNotFoundException, SQLException, TestarServiceException {
        if (!dockerPoolService.isDockerAvailable()) {
            throw new TestarServiceException(TestarServiceException.DOCKER_UNAVAILABLE);
        }

        System.out.println("Creating MYSQL docker image");
        dockerPoolService.start("reporting");


        if (delegate != null) {
            delegate.onStateChanged(MySqlServiceDelegate.State.BUILDING_IMAGE, "Building database image");
        }
        final String imageId = dockerPoolService.buildImage(new File(Main.databaseDir),
                "FROM mysql:latest\n" +
                "ENV MYSQL_ROOT_PASSWORD=" + userPassword + "\n" +
                "ENV MYSQL_DATABASE=" + databaseName + "\n" +
                "ENV MYSQL_USER=" + userName + "\n" +
                "ENV MYSQL_PASSWORD=" + userPassword);

        if (delegate != null) {
            delegate.onStateChanged(MySqlServiceDelegate.State.STARTING_SERVICE, "Starting local database");
        }

        final File databaseDir = new File(settings.get(ConfigTags.SQLReportingDirectory));
        if (databaseDir.isDirectory()) {
            System.out.println("Directory exists: " + databaseDir.getAbsolutePath());
        }
        else {
            System.out.println("Directory does not exist: " + databaseDir.getAbsolutePath());
        }
        final File databaseInitDir = new File(Main.databaseDir + File.separator + LOCAL_DATABASE_INIT_PATH);
        if (databaseInitDir.isDirectory()) {
            System.out.println("Directory exists: " + databaseInitDir.getAbsolutePath());
        }
        else {
            System.out.println("Directory does not exist: " + databaseInitDir.getAbsolutePath());
        }
//        final String databaseInitPath = Main.extrasDir + File.separator + LOCAL_DATABASE_INIT_PATH;
        final HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse("13306:3306"))
                .withBinds(
                        new Bind(databaseDir.getAbsolutePath(), new Volume("/var/lib/mysql")),
                        new Bind(databaseInitDir.getAbsolutePath(), new Volume("/docker-entrypoint-initdb.d"))
                );
        dockerPoolService.startWithImage(imageId, "mysql", hostConfig);

        if (delegate != null) {
            delegate.onStateChanged(MySqlServiceDelegate.State.CONNECTING, "Connecting local database");
        }

        Class.forName("com.mysql.jdbc.Driver");
        final String url = "jdbc:mysql://localhost:13306/" + databaseName +
                "?user=" + userName + "&password=" + userPassword;
        while (connection == null) {
            try {
                connection = DriverManager.getConnection(url);
            }
            catch (CommunicationsException e) {
                System.out.println("Still not ready");
                try {
                    Thread.sleep(2000);
                }
                catch (Exception ex) {}
            }
        }
        lastIdStatement = connection.prepareStatement(LAST_ID_QUERY);
        if (delegate != null) {
            delegate.onServiceReady(url);
        }
    }

    public synchronized void connectExternalDatabase(String hostname, String databaseName, String userName, String userPassword) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + databaseName +
                "?user=" + userName + "&password=" + userPassword);
        lastIdStatement = connection.prepareStatement(LAST_ID_QUERY);
    }

    public synchronized int registerReport(String tag) throws SQLException {
        PreparedStatement addReportStatement = connection.prepareStatement("INSERT INTO reports (tag, time) VALUES (?, NOW())");
        addReportStatement.setString(1, tag);
        addReportStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized int registerIteration(int reportId) throws SQLException {
        PreparedStatement addSequenceStatement = connection.prepareStatement("INSERT INTO iterations (report_id) VALUES (?)");
        addSequenceStatement.setInt(1, reportId);
        addSequenceStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized int registerIteration(int reportId, String info, Double severity) throws SQLException {
        PreparedStatement addSequenceStatement = connection.prepareStatement("INSERT INTO iterations (report_id, info, severity) VALUES (?, ?, ?)");
        addSequenceStatement.setInt(1, reportId);
        addSequenceStatement.setString(2, info);
        addSequenceStatement.setDouble(3, severity);
        addSequenceStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized int registerAction(String name, String description, String status, String screenshot, Timestamp startTime) throws SQLException {
        PreparedStatement addActionStatement = connection.prepareStatement("INSERT INTO actions (name, description, status, screenshot, start_time) VALUES (?, ?, ?, ?, ?)");
        addActionStatement.setString(1, name);
        addActionStatement.setString(2, description);
        addActionStatement.setString(3, status);
        addActionStatement.setString(4, screenshot);
        addActionStatement.setTimestamp(5, startTime);
        addActionStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized void addActionToIteration(int actionId, int iterationId) throws SQLException {
        PreparedStatement updateActionStatement = connection.prepareStatement("UPDATE actions SET iteration_id=? WHERE id=?");
        updateActionStatement.setInt(1, iterationId);
        updateActionStatement.setInt(2, actionId);

        updateActionStatement.executeUpdate();
    }

    public synchronized void setSelectionInIteration(int iterationId, int lastExecutedActionId, int lastStateId) throws SQLException {
        PreparedStatement setSelectionStatement = connection.prepareStatement("UPDATE iterations SET last_executed_action_id=?, last_state_id=? WHERE id=?");
        setSelectionStatement.setInt(1, lastExecutedActionId);
        setSelectionStatement.setInt(2, lastStateId);
        setSelectionStatement.setInt(3, iterationId);

        setSelectionStatement.executeUpdate();
    }

    public synchronized int registerState(String concreteIdCustom, String abstractId, String abstractRId, String abstractRTId, String abstractRTPId) throws SQLException {
        PreparedStatement addStateStatement = connection.prepareStatement("INSERT INTO sequence_items (concrete_id, abstract_id, abstract_r_id, abstract_r_t_id, abstract_r_t_p_id) VALUES (?, ?, ?, ?, ?)");
        addStateStatement.setString(1, concreteIdCustom);
        addStateStatement.setString(2, abstractId);
        addStateStatement.setString(3, abstractRId);
        addStateStatement.setString(4, abstractRTId);
        addStateStatement.setString(5, abstractRTPId);
        addStateStatement.executeUpdate();

        final ResultSet resultSet = lastIdStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized int findState(String concreteIdCustom, String abstractId) throws SQLException {
        PreparedStatement findStateStatement = connection.prepareStatement("SELECT id FROM sequence_items WHERE concrete_id = ? AND abstract_id = ?");
        findStateStatement.setString(1, concreteIdCustom);
        findStateStatement.setString(2, abstractId);

        final ResultSet resultSet = findStateStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    @Override
    public int getReportId(String reportTag) throws SQLException {
        PreparedStatement selectReportIdStatement = connection.prepareStatement("SELECT id FROM report WHERE tag = ?");
        selectReportIdStatement.setString(1, reportTag);

        final ResultSet resultSet = selectReportIdStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    @Override
    public int getFirstIterationId(int reportId) throws SQLException {
        PreparedStatement selectFirstIterationIdStatement = connection.prepareStatement("SELECT id FROM iterations WHERE report_id = ? ORDER BY id LIMIT 1");
        selectFirstIterationIdStatement.setInt(1, reportId);

        final ResultSet resultSet = selectFirstIterationIdStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    @Override
    public int getNextIterationId(int reportId, int iterationId) throws SQLException {
        PreparedStatement selectNextIterationIdStatement = connection.prepareStatement("SELECT id FROM iterations WHERE report_id = ? AND id > ? ORDER BY id LIMIT 1");
        selectNextIterationIdStatement.setInt(1, reportId);
        selectNextIterationIdStatement.setInt(2, iterationId);

        final ResultSet resultSet = selectNextIterationIdStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return -1;
    }

    @Override
    public ActionData getFirstAction(int iterationId) throws SQLException {
        PreparedStatement selectFirstActionStatement = connection.prepareStatement("SELECT id, name, description, status, screenshot, start_time FROM actions WHERE iteration_id = ? ORDER BY start_time LIMIT 1");
        selectFirstActionStatement.setInt(1, iterationId);

        final ResultSet resultSet = selectFirstActionStatement.executeQuery();
        if (resultSet.next()) {
            int id = resultSet.getInt(1);
            final String name = resultSet.getString(2);
            final String description = resultSet.getString(3);
            final String status = resultSet.getString(4);
            final String screenshot = resultSet.getString(5);
            final Timestamp startTime = resultSet.getTimestamp(6);

            return new ActionData(id, iterationId, name, description, status, screenshot, startTime);
        }
        return null;
    }

    @Override
    public ActionData getNextAction(int iterationId, Timestamp actionTime) throws SQLException {
        PreparedStatement selectNextActionStatement = connection.prepareStatement("SELECT id, name, description, status, screenshot, start_time FROM actions WHERE iteration_id = ? AND start_time > ? ORDER BY start_time LIMIT 1");
        selectNextActionStatement.setInt(1, iterationId);
        selectNextActionStatement.setTimestamp(2, actionTime);

        final ResultSet resultSet = selectNextActionStatement.executeQuery();
        if (resultSet.next()) {
            int id = resultSet.getInt(1);
            final String name = resultSet.getString(2);
            final String description = resultSet.getString(3);
            final String status = resultSet.getString(4);
            final String screenshot = resultSet.getString(5);
            final Timestamp startTime = resultSet.getTimestamp(6);

            return new ActionData(id, iterationId, name, description, status, screenshot, startTime);
        }
        return null;
    }

    public synchronized void storeVerdict(int iterationId, String info, Double severity) throws SQLException {
        PreparedStatement updateVerdictStatement = connection.prepareStatement("UPDATE iterations SET info=?, severity=? WHERE id=?");
        updateVerdictStatement.setString(1, info);
        updateVerdictStatement.setDouble(2, severity);
        updateVerdictStatement.setInt(3, iterationId);

        updateVerdictStatement.executeUpdate();
    }

    public synchronized void finalizeReport(int reportId, int actionsPerSequence, int totalSequences, String url) throws SQLException {
        PreparedStatement updateVerdictStatement = connection.prepareStatement("UPDATE reports SET actions_per_sequence=?, total_sequences=?, url=? WHERE id=?");
        updateVerdictStatement.setInt(1, actionsPerSequence);
        updateVerdictStatement.setInt(2, totalSequences);
        updateVerdictStatement.setString(3, url);
        updateVerdictStatement.setInt(4, reportId);

        updateVerdictStatement.executeUpdate();
    }

//    public synchronized void stopLocalDatabase() { dockerPoolService.dispose(false);
//    }

    public DockerPoolService getDockerPoolService() {
        return dockerPoolService;
    }
}
