package org.testar.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://";

    public static Connection getConnection(String url, String port, String user, String password, String databasename) throws SQLException {
        return DriverManager.getConnection(URL+url+":"+port+"/"+databasename, user, password);
    }


}