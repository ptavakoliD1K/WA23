package de.filetransfer.controller;

import de.filetransfer.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteFileController {

    @Autowired
    private DatabaseService databaseService;

    /**
     * deletes file from database
     * @param name
     * @return
     */

    @DeleteMapping("/delete-file")
    public String deleteFile(@RequestParam String name) {
        boolean result = databaseService.deleteFile(name);
        return result ? "File deleted successfully" : "File not found";
    }
}
