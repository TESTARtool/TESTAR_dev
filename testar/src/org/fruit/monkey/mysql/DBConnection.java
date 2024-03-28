package org.fruit.monkey.mysql;

import org.fruit.monkey.btrace.MethodInvocation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:";

    public static Connection getConnection(String port, String user, String password, String databasename) throws SQLException {
        return DriverManager.getConnection(URL+port+"/"+databasename, user, password);
    }


}