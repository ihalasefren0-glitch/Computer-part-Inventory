/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventory;

/**
 *
 * @author ACER
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Connection {

    private static final String URL = "jdbc:mysql://localhost:3306/Inventory"
            + "?createDatabaseIfNotExist=true"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Manila";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL driver was not found. In NetBeans, add lib/mysql-connector-j-9.4.0.jar to Libraries.",
                    e);
        }

        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
        ensureSchema(con);
        System.out.println("Database Connected!");
        return con;
    }

    private static void ensureSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS products ("
                    + "id INT NOT NULL AUTO_INCREMENT,"
                    + "product_name VARCHAR(100) NOT NULL,"
                    + "brand VARCHAR(100) NOT NULL DEFAULT '',"
                    + "model VARCHAR(100) NOT NULL DEFAULT '',"
                    + "category VARCHAR(100) NOT NULL DEFAULT '',"
                    + "price DECIMAL(10,2) NOT NULL DEFAULT 0,"
                    + "stock INT NOT NULL DEFAULT 0,"
                    + "status VARCHAR(30) NOT NULL DEFAULT 'In Stock',"
                    + "PRIMARY KEY (id))"
            );
        }
        addColumnIfMissing(con, "brand", "VARCHAR(100) NOT NULL DEFAULT ''");
        addColumnIfMissing(con, "model", "VARCHAR(100) NOT NULL DEFAULT ''");
        addColumnIfMissing(con, "category", "VARCHAR(100) NOT NULL DEFAULT ''");
        addColumnIfMissing(con, "price", "DECIMAL(10,2) NOT NULL DEFAULT 0");
        addColumnIfMissing(con, "stock", "INT NOT NULL DEFAULT 0");
        addColumnIfMissing(con, "status", "VARCHAR(30) NOT NULL DEFAULT 'In Stock'");
        ensureUsers(con);
    }

    private static void ensureUsers(Connection con) throws SQLException {
        createAccountTable(con, "admin");
        createAccountTable(con, "employee");
        if (tableExists(con, "users")) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate(
                        "INSERT INTO admin (Fullname, Address, Username, Password, AccountStatus) "
                        + "SELECT Fullname, Address, Username, Password, AccountStatus FROM users "
                        + "WHERE LOWER(AccountType) = 'admin' "
                        + "AND Username NOT IN (SELECT Username FROM admin)"
                );
                st.executeUpdate(
                        "INSERT INTO employee (Fullname, Address, Username, Password, AccountStatus) "
                        + "SELECT Fullname, Address, Username, Password, AccountStatus FROM users "
                        + "WHERE LOWER(AccountType) = 'employee' "
                        + "AND Username NOT IN (SELECT Username FROM employee)"
                );
                st.executeUpdate("DROP TABLE users");
            }
        }
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "INSERT INTO admin (Fullname, Address, Username, Password, AccountStatus) "
                    + "SELECT 'Administrator', '', 'Admin', '12345', 'Active' FROM DUAL "
                    + "WHERE NOT EXISTS (SELECT 1 FROM admin WHERE Username = 'Admin')"
            );
            st.executeUpdate(
                    "INSERT INTO employee (Fullname, Address, Username, Password, AccountStatus) "
                    + "SELECT 'Employee', '', 'Employee', '12345', 'Active' FROM DUAL "
                    + "WHERE NOT EXISTS (SELECT 1 FROM employee WHERE Username = 'Employee')"
            );
        }
    }

    private static void createAccountTable(Connection con, String table) throws SQLException {
        if (tableExists(con, table)) {
            return;
        }
        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE " + table + " ("
                    + "UserID INT NOT NULL AUTO_INCREMENT,"
                    + "Fullname VARCHAR(100) NOT NULL,"
                    + "Address VARCHAR(255) NOT NULL DEFAULT '',"
                    + "Username VARCHAR(50) NOT NULL,"
                    + "Password VARCHAR(50) NOT NULL,"
                    + "AccountStatus VARCHAR(20) NOT NULL DEFAULT 'Active',"
                    + "PRIMARY KEY (UserID),"
                    + "UNIQUE KEY Username (Username))"
            );
        }
    }

    private static boolean tableExists(Connection con, String table) throws SQLException {
        String check = "SELECT COUNT(*) FROM information_schema.TABLES "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
        try (PreparedStatement ps = con.prepareStatement(check)) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static boolean columnExists(Connection con, String table, String column) throws SQLException {
        String check = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement ps = con.prepareStatement(check)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static void addColumnIfMissing(Connection con, String column, String definition) throws SQLException {
        String check = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'products' AND COLUMN_NAME = ?";
        try (PreparedStatement ps = con.prepareStatement(check)) {
            ps.setString(1, column);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (Statement st = con.createStatement()) {
                        st.executeUpdate("ALTER TABLE products ADD COLUMN " + column + " " + definition);
                    }
                }
            }
        }
    }
}
