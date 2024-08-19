package com.quickwave.QuickWave.controller;
import com.quickwave.QuickWave.Entity.File;
import com.quickwave.QuickWave.service.EmailService;
import com.quickwave.QuickWave.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            File uploadedFile = fileService.store(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("File uploaded successfully: http://localhost:8080/api/files/" + uploadedFile.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long id) {
        File file = fileService.getFile(id);
        if (file != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(new ByteArrayResource(file.getData()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/all")
    public ResponseEntity<List<File>> getAllFiles() {
        List<File> files = fileService.getAllFiles();

        // Debug log to print the number of files fetched
        System.out.println("Fetched files count: " + files.size());

        if (files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(files);
        }
        return ResponseEntity.ok(files);
    }

    @PostMapping("/sendFileLink")
    public ResponseEntity<String> sendFileLink(@RequestParam("email") String email, @RequestParam("fileId") Long fileId) {
        File file = fileService.getFile(fileId);
        if (file != null) {
            String fileLink = "http://localhost:8080/api/files/" + file.getId();
            String subject = "Download Your File";
            String text = "You can download the file using the following link: " + fileLink;

            emailService.sendFileLinkEmail(email, subject, text);
            return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully to: " + email);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
    }
}