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
     * @param semester
     * @param module
     * @param fachrichtung
     * @return
     */

    @DeleteMapping("/delete-file")
    public String deleteFile(
            @RequestParam String name,
            @RequestParam("semester") String semester,
            @RequestParam("module") String module,
            @RequestParam("fachrichtung") String fachrichtung

    ) {
        boolean result = databaseService.deleteFile(name, semester, module, fachrichtung);
        return result ? "File deleted successfully" : "File not found";
    }
}
