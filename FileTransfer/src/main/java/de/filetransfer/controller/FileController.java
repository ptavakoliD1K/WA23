package de.filetransfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import com.google.gson.Gson;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final String DB_URL = "jdbc:sqlite:C:/PorjektFH/Datenbank/PDFDatabank.db";

    /**
     * gets file list, calls getFileNamesFromDatabase()
     * @return
     */

    @GetMapping("/list")
    public ResponseEntity<String> getFileList() {
        List<String> fileNames;
        try {
            fileNames = getFileNamesFromDatabase();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>("{\"error\": \"Fehler beim Abrufen der Dateiliste\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Gson gson = new Gson();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(gson.toJson(fileNames));
    }

    /**
     * adds download function, calls getFileNamesFromDatabase()
     * @param fileName
     * @return
     */

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) {
        byte[] fileData;
        try {
            fileData = getFileFromDatabase(fileName);
            if (fileData != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName);

                return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
            } else {
                // Returning an empty byte array with a `404 Not Found` status
                return new ResponseEntity<>(new byte[0], HttpStatus.NOT_FOUND);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Returning an empty byte array with a `500 Internal Server Error` status
            return new ResponseEntity<>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * gets file names from files in database
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */

    private List<String> getFileNamesFromDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        List<String> fileNames = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String selectSQL = "SELECT name FROM files";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fileNames.add(rs.getString("name"));
                }
            }
        }
        return fileNames;
    }

    /**
     * gets content of files from database
     * @param fileName
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */

    private byte[] getFileFromDatabase(String fileName) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String selectSQL = "SELECT content FROM files WHERE name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, fileName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBytes("content");
                    }
                }
            }
        }
        return null;
    }


    @Autowired
    private DataSource dataSource;

    @GetMapping("/preview")
    public ResponseEntity<InputStreamResource> previewFile(@RequestParam String name) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT content FROM files WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                byte[] fileContent = resultSet.getBytes("content");
                InputStream inputStream = new ByteArrayInputStream(fileContent);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "inline;filename=" + name);

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(determineMediaType(name))
                        .body(new InputStreamResource(inputStream));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private MediaType determineMediaType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else if (fileName.matches("(?i).+\\.(jpg|jpeg|png|gif)$")) {
            return MediaType.IMAGE_JPEG;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
