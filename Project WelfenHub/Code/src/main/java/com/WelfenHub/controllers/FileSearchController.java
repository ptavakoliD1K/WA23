package com.WelfenHub.controllers;

import de.filetransfer.service.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FileSearchController {

    /**
     * searches the files in the database with the properties
     * @param query
     * @param semester
     * @param module
     * @param fachrichtung
     * @return
     */

    @GetMapping("/api/search")
    public List<FileDTO> searchFiles(
            @RequestParam String query,
            @RequestParam("semester") String semester,
            @RequestParam("module") String module,
            @RequestParam("fachrichtung") String fachrichtung
        ) {
        List<FileDTO> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/PorjektFH/Welfenhub2/Project WelfenHub/database/file_upload.db")) {
            String sql = "SELECT id, name FROM files WHERE name LIKE ? AND semester LIKE ? AND module LIKE ? AND fachrichtung LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + query + "%");
                pstmt.setString(2, semester);
                pstmt.setString(3, module);
                pstmt.setString(4, fachrichtung);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        FileDTO fileDTO = new FileDTO();
                        fileDTO.setId(rs.getLong("id"));
                        fileDTO.setName(rs.getString("name"));
                        results.add(fileDTO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
