package de.filetransfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
public class FileUploadController {

    @Autowired
    private DataSource dataSource;

    /**
     * handles file uploads; sends to saveFileToDatabase()
     * @param files
     * @return
     */

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("files[]") MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                byte[] fileBytes = inputStreamToByteArray(file.getInputStream());
                saveFileToDatabase(fileName, fileBytes);
            }
            return ResponseEntity.status(HttpStatus.OK).body("Dateien erfolgreich hochgeladen!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hochladen fehlgeschlagen!");
        }
    }

    /**
     * input of data to the system
     * @param inputStream
     * @return
     * @throws IOException
     */

    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    /**
     * upload file to database and put it into table
     * @param fileName
     * @param fileBytes
     * @throws SQLException
     */

    private void saveFileToDatabase(String fileName, byte[] fileBytes) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String insertSQL = "INSERT INTO files (name, content) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, fileName);
                pstmt.setBytes(2, fileBytes);
                pstmt.executeUpdate();
            }
        }
    }
}
