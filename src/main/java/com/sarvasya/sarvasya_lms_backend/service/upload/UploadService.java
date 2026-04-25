package com.sarvasya.sarvasya_lms_backend.service.upload;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.upload.UploadVideoResponse;

public interface UploadService {
    UploadVideoResponse uploadVideo(MultipartFile file);

    Resource getVideo(String fileName);
}








