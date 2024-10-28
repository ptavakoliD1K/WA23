package de.filetransfer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

    public class CreateDatabase {

        public static void createNewDatabase(String fileName) {
            String url = "jdbc:sqlite:" + fileName;

            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("A new database has been created.");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        public static void createTables(String fileName) {
            String url = "jdbc:sqlite:" + fileName;

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {

                // Erstelle die Tabellen Semester1 bis Semester6
                for (int i = 1; i <= 6; i++) {
                    String tableName = "Semester" + i;
                    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                            + " name TEXT NOT NULL,\n"
                            + " content BLOB NOT NULL\n"
                            + ");";

                    // Führe die SQL-Anweisung aus
                    stmt.execute(sql);
                    System.out.println("Table " + tableName + " has been created.");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        public static void main(String[] args) {
            String fileName = "university.db";

            // Datenbank erstellen
            createNewDatabase(fileName);

            // Tabellen erstellen
            createTables(fileName);
        }
    }


