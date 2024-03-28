package org.fruit.monkey.btrace;

import lombok.AllArgsConstructor;
import org.fruit.monkey.javaparser.JavaUnit;
import org.fruit.monkey.javaparser.MethodDeclaration;
import org.fruit.monkey.mysql.DBConnection;
import org.fruit.monkey.mysql.SerializationUtil;
import org.fruit.monkey.sonarqube.api.SQIssue;
import org.fruit.monkey.weights.Issue;

import javax.sql.rowset.serial.SerialBlob;
import java.io.FileInputStream;
import java.sql.*;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;


public class DBStaticAnalysisRepository {

    Connection connection;

    public DBStaticAnalysisRepository(){
        try {
            connection = DBConnection.getConnection("33306", "testar", "testar", "testar");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


//    public void saveIssue(Issue issue, JavaUnit javaUnit, MethodDeclaration method) throws SQLException {
//        String insertIssueSql = "INSERT INTO Issue (severity, class_id, method_id, message) VALUES (?, (SELECT class_id FROM Class WHERE class_name = ?), (SELECT method_id FROM Method WHERE name = ? AND class_id = (SELECT class_id FROM Class WHERE class_name = ?)), ?);";
//        PreparedStatement pstmt = connection.prepareStatement(insertIssueSql);
//        pstmt.setString(1, issue.getSeverity());
//        pstmt.setString(2, javaUnit.getLocation());
//        pstmt.setString(3, method != null ? method.getName() : null);
//        pstmt.setString(4, javaClass.getLocation());
//        pstmt.setString(5, issue.getMessage());
//        pstmt.executeUpdate();
//    }


    // Example method to fetch methodId by classId and method name
    public Integer getMethodIdByClassAndName(Integer classId, String methodName) throws SQLException {
        String query = "SELECT method_id FROM Method WHERE class_id = ? and name = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, classId);
        pstmt.setString(2, methodName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("method_id");
            }
        }
        return -1;
    }

    public void saveIssueWithoutMethod(SQIssue issue, JavaUnit javaUnit) throws SQLException {

        String className = "DefaultClass";
        if(javaUnit!=null){
            className = javaUnit.getUnitName();
        }
        int class_id = getClassIdByName(className);
        System.out.println(class_id);
        String insertIssueSql = "INSERT INTO Issue (issue_id, severity, class_id, message) VALUES (?, ?, ?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(insertIssueSql);
        pstmt.setString(1, issue.getKey());
        pstmt.setString(2, issue.getSeverity());
        pstmt.setInt(3, class_id );
        pstmt.setString(4, issue.getMessage());
        System.out.println(insertIssueSql);
        pstmt.executeUpdate();
        System.out.println("DONE");
    }

    public void saveIssueWithMethod(SQIssue issue, JavaUnit javaUnit, MethodDeclaration method) throws SQLException {
        int class_id = getClassIdByName(javaUnit.getUnitName());

        if (class_id<0) {
            System.out.println("Could not find class " + javaUnit.getUnitName() );
        }
        else{
            System.out.println("Finding method");
            int method_id = getMethodIdByClassAndName(class_id, method.getName());
            System.out.println("Found method");

            String insertIssueSql = "INSERT INTO Issue (issue_id, severity, class_id, method_id, message) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement pstmt = connection.prepareStatement(insertIssueSql);
            pstmt.setString(1, issue.getKey());
            pstmt.setString(2, issue.getSeverity());
            pstmt.setInt(3, class_id );
            String text;
            if(method_id<0){
                pstmt.setNull(4, Types.INTEGER);
                text = "null";
            }
            else{
                pstmt.setInt(4, method_id);
                text = String.valueOf(method_id);
            }
            pstmt.setString(5, issue.getMessage());

            pstmt.executeUpdate();
        }
    }



    public void saveClass(JavaUnit javaunit) throws SQLException {
        String insertClassSql = "INSERT INTO Class (class_name) VALUES (?) ON DUPLICATE KEY UPDATE class_name=VALUES(class_name);";

        PreparedStatement pstmt = connection.prepareStatement(insertClassSql);
        pstmt.setString(1, javaunit.getUnitName());
        pstmt.executeUpdate();

    }

    public void saveClass(String className) throws SQLException {
        String insertClassSql = "INSERT INTO Class (class_name) VALUES (?) ON DUPLICATE KEY UPDATE class_name=VALUES(class_name);";

        PreparedStatement pstmt = connection.prepareStatement(insertClassSql);
        pstmt.setString(1, className);
        pstmt.executeUpdate();

    }

    private int getClassIdByName(String className) throws SQLException {
        String query = "SELECT class_id FROM Class WHERE class_name = ?;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, className);
        System.out.println("BEFORE");
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("class_id");
            }
        }
        return -1; // Class ID not found
    }


    public String getIssueByID(String issueID) throws SQLException {
        String query = "SELECT issue_id FROM Issue WHERE issue_id = ?;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, issueID);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("issue_id");
            }
        }
        return null; // Class ID not found
    }

    public void saveMethod(JavaUnit javaUnit, MethodDeclaration method) throws SQLException {
        int classID = getClassIdByName(javaUnit.getUnitName());

        if(classID>0) {
            saveMethod(classID, method);
        }
    }

    public void saveMethod(int class_id, MethodDeclaration method) throws SQLException {
        String insertMethodSql = "INSERT INTO Method (class_id, name, params) VALUES (?, ?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(insertMethodSql);
        pstmt.setInt(1, class_id);
        pstmt.setString(2, method.getName());
        pstmt.setBlob(3, new SerialBlob(SerializationUtil.serializeList(method.getParameters()))); // Assuming params is a Byte
        System.out.println(insertMethodSql);
        pstmt.executeUpdate();
    }


}
