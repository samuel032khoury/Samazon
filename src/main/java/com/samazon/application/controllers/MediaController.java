package com.samazon.application.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.samazon.application.dto.common.APIResponse;
import com.samazon.application.services.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MediaController {

    private final FileService fileService;

    @PostMapping("/media")
    public ResponseEntity<APIResponse> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileService.uploadMedia(file);
        return ResponseEntity.ok(new APIResponse("File " + fileName + " uploaded successfully", true));
    }

}
