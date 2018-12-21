package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

public class Config {

    // type of orientdb installation, could be remote, integrated, etc
    private String connectionType;

    // server location
    private String server;

    // database name
    private String database;

    // database user
    private String user;

    // database password
    private String password;

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
}
