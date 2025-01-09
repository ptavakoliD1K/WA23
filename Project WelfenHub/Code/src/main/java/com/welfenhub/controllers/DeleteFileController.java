package com.welfenhub.controllers;

import com.welfenhub.services.DatabaseService;
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
     * @param tag
     * @return
     */

    @DeleteMapping("/delete-file")
    public String deleteFile(
            @RequestParam String name,
            @RequestParam("semester") String semester,
            @RequestParam("module") String module,
            @RequestParam("fachrichtung") String fachrichtung,
            @RequestParam("tag") String tag

    ) {
        boolean result = databaseService.deleteFile(name, semester, module, fachrichtung, tag);
        return result ? "File deleted successfully" : "File not found";
    }
}
