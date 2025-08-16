package com.samazon.application.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    @Value("${project.media.upload.dir}")
    private String UPLOAD_DIR;

    @Override
    public String uploadMedia(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        String randomId = UUID.randomUUID().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = randomId + fileExtension;
        String fullPath = UPLOAD_DIR + File.separator + newFileName;

        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + UPLOAD_DIR);
            }
        }
        Files.copy(file.getInputStream(), Paths.get(fullPath));

        return newFileName;
    }

}
