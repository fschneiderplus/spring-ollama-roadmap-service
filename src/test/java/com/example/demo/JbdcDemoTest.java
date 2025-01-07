package com.example.demo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JbdcDemoTest {
    static final String JDBC_URL = "jdbc:h2:~/test";
    static final String JDBC_USER = "sa";
    static final String JDBC_PASSWORD = "";

    @Test
    void shouldACreateTable() {
        Connection con = null;
        Statement stmt = null;
        try {
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldBInsertData() {
        Connection con = null;
        Statement stmt = null;
        try {
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            stmt = con.createStatement();
            stmt.execute("INSERT INTO TEST VALUES(1, 'Hello')");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldCSelectData() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TEST");
            while (rs.next()) {
                System.out.println(rs.getInt("ID") + " " + rs.getString("NAME"));
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDDeleteData() {
        Connection con = null;
        Statement stmt = null;
        try {
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            stmt = con.createStatement();
            stmt.execute("DELETE FROM TEST WHERE ID = 1");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldEDropTable() {
        Connection con = null;
        Statement stmt = null;
        try {
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            stmt = con.createStatement();
            stmt.execute("DROP TABLE TEST");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
