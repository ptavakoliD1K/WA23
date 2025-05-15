package de.filetransfer.controller;

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

    @GetMapping("/api/search")
    public List<FileDTO> searchFiles(@RequestParam String query) {
        List<FileDTO> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/PorjektFH/Datenbank/PDFDatabank.db")) {
            String sql = "SELECT id, name FROM files WHERE name LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + query + "%");
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
