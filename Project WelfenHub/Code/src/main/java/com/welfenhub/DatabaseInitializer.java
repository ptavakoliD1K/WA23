package com.welfenhub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void main(String[] args) {
        // PostgreSQL-Verbindungsdetails
        String url = "jdbc:postgresql://217.160.18.102:5432/welfenhub";
        String user = "welfenadmin";
        String password = "meinSicheresPasswort";

        // SQL-Befehl zum Erstellen der Tabelle (angepasst für PostgreSQL)
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS files (
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                content BYTEA NOT NULL,
                semester TEXT NOT NULL,
                module TEXT NOT NULL,
                fachrichtung TEXT NOT NULL
            );
            """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // Verbindung und Tabelle erstellen
            stmt.execute(createTableSQL);
            System.out.println("Datenbank und Tabelle 'files' erfolgreich erstellt oder bereits vorhanden.");

        } catch (Exception e) {
            System.err.println("Fehler beim Erstellen der Datenbanktabelle: " + e.getMessage());
        }
    }
}
