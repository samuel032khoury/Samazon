package com.samazon.application.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadMedia(String path, MultipartFile file) throws IOException;
}
