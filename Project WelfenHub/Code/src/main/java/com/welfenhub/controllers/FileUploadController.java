package com.welfenhub.controllers;

import com.welfenhub.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private FileRepository fileRepository;

    /**
     * takes the information of the uploaded files and sends it to saveFileToDatabase()
     * @param files
     * @param semester
     * @param module
     * @param fachrichtung
     * @param tag
     * @return
     */

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload1(
            @RequestParam("files[]") MultipartFile[] files,
            @RequestParam("semester") String semester,
            @RequestParam("module") String module,
            @RequestParam("fachrichtung") String fachrichtung,
            @RequestParam("tag") String tag
    ) {
        System.out.println("Test");
        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                byte[] fileBytes = inputStreamToByteArray(file.getInputStream());
                saveFileToDatabase(fileName, fileBytes, semester, module, fachrichtung, tag);
            }
            return ResponseEntity.status(HttpStatus.OK).body("Dateien erfolgreich hochgeladen!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hochladen fehlgeschlagen!");
        }
    }

    /**
     * reads data of inputStream and puts it into byte[] array
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
     * uploads file to database with all properties
     * @param fileName
     * @param fileBytes
     * @param semester
     * @param module
     * @param fachrichtung
     * @param tag
     * @throws SQLException
     * calls repository to upload file to database
     */

    private void saveFileToDatabase(String fileName, byte[] fileBytes, String semester, String module, String fachrichtung, String tag) throws SQLException {
        fileRepository.uploadFile(fileName, fileBytes, semester, module, fachrichtung, tag);
    }
}
