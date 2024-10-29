package com.WelfenHub.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class DatabaseService {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    /**
     * deletes file from database
     * @param name
     * @param semester
     * @param module
     * @param fachrichtung
     * @return
     */

    public boolean deleteFile(String name, String semester, String module, String fachrichtung) {
        String sql = "DELETE FROM files WHERE name = ? AND semester = ? AND module = ? AND fachrichtung = ?";

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, semester);
            pstmt.setString(3, module);
            pstmt.setString(4, fachrichtung);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
