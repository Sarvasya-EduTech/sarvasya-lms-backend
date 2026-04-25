package com.sarvasya.sarvasya_lms_backend.service.impl.upload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.sarvasya.sarvasya_lms_backend.api.NotFoundException;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.upload.UploadVideoResponse;
import com.sarvasya.sarvasya_lms_backend.service.upload.UploadService;

@Service
public class UploadServiceImpl implements UploadService {

    private static final String UPLOAD_DIR = "uploads/sarvasya/videos";

    @Override
    public UploadVideoResponse uploadVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = Objects.requireNonNullElse(file.getOriginalFilename(), "video.bin");
            String fileName = System.currentTimeMillis() + "_" + originalName.replace("..", "");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/sarvasya/upload/videos/" + fileName;
            return new UploadVideoResponse("Video uploaded successfully", fileUrl);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to upload video");
        }
    }

    @Override
    public Resource getVideo(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("Video not found");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Invalid video path");
        }
    }
}








