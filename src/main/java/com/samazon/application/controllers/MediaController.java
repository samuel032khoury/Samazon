package com.samazon.application.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.services.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final FileService fileService;

    @Value("${project.media.upload.dir}")
    private String uploadDir;

    @PostMapping("/admin/upload")
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileService.uploadMedia(uploadDir, file);
        Map<String, String> response = Map.of("fileName", fileName);
        response.put("message", "File uploaded successfully");
        return ResponseEntity.ok(response);
    }
}
