/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventory;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection con = DB_Connection.getConnection()) {
            System.out.println("Ready.");
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
    }
}
