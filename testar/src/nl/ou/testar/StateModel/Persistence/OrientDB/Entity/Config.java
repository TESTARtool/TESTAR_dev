package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

public class Config {

    // connection type used when connecting to a locally running OrientDB
    public static final String CONNECTION_TYPE_LOCAL = "plocal";

    // connection type used when connecting to OrientDB running in remote server mode
    public static final String  CONNECTION_TYPE_REMOTE = "remote";

    // type of orientdb installation, could be remote, integrated, etc
    private String connectionType;

    // server location (for remote connections)
    private String server;

    // local database directory (for local connections)
    private String databaseDirectory;

    // database name
    private String database;

    // database user
    private String user;

    // database password
    private String password;

    // create/reset the database?
    private boolean resetDataStore;

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean resetDataStore() {
        return resetDataStore;
    }

    public void setResetDataStore(boolean resetDataStore) {
        this.resetDataStore = resetDataStore;
    }

    public String getDatabaseDirectory() {
        return databaseDirectory;
    }

    public void setDatabaseDirectory(String databaseDirectory) {
        this.databaseDirectory = databaseDirectory;
    }
}
