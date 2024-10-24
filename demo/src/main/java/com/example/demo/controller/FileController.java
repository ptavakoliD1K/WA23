package com.example.demo.controller;

import com.example.demo.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        model.addAttribute("files", fileStorageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
                                "serveFile", path.getFileName().toString()).build().toUriString())
                .collect(Collectors.toList()));
        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        System.out.println("Request to serve file: " + filename); // Log the filename requested
        Resource resource = fileStorageService.load(filename);

        if (resource.exists() && resource.isReadable()) {
            System.out.println("File found and is readable: " + resource.getFilename());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            System.out.println("File not found or is not readable: " + filename);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            fileStorageService.save(file);
            model.addAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (Exception e) {
            model.addAttribute("message", "Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }
        return "redirect:/";
    }
}
