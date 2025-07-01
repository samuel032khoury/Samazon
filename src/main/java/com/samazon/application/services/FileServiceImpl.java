package com.samazon.application.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    // Implement the uploadMedia method here
    @Override
    public String uploadMedia(String path, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        String randomId = UUID.randomUUID().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = randomId + fileExtension;
        String fullPath = path + File.separator + newFileName;

        File folder = new File(path);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + path);
            }
        }
        Files.copy(file.getInputStream(), Paths.get(fullPath));

        return newFileName;
    }

}
