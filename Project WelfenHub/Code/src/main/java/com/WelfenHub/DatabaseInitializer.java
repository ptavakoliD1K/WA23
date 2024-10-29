package com.WelfenHub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void main(String[] args) {
        // Der Pfad zur SQLite-Datenbankdatei
        String url = "jdbc:sqlite:./database/users.db";

        // SQL-Befehl zum Erstellen der Tabelle
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS files (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                content BLOB NOT NULL,
                semester TEXT NOT NULL,
                module TEXT NOT NULL,
                fachrichtung TEXT NOT NULL
            );
            """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // Verbindung und Tabelle erstellen
            stmt.execute(createTableSQL);
            System.out.println("Datenbank und Tabelle 'files' erfolgreich erstellt oder bereits vorhanden.");

        } catch (Exception e) {
            System.err.println("Fehler beim Erstellen der Datenbanktabelle: " + e.getMessage());
        }
    }
}
