import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // Methode zum Erstellen der SQLite-Datenbank und Tabelle
    public static void createDatabaseAndTable(String dbUrl) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            if (conn != null) {
                // Statement-Objekt zum Ausführen von SQL-Befehlen erstellen
                try (Statement stmt = conn.createStatement()) {
                    // SQL-Befehl zum Erstellen der Tabelle
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS files (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "content BLOB NOT NULL" +
                            ");";
                    // Tabelle erstellen
                    stmt.execute(createTableSQL);
                    System.out.println("Datenbank und Tabelle wurden erfolgreich erstellt.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Hauptmethode zum Testen
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:C:/PorjektFH/Datenbank/PDFDatabank.db";
        createDatabaseAndTable(dbUrl);
    }
}
