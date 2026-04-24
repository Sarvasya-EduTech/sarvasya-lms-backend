package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.upload.UploadVideoResponse;
import com.sarvasya.sarvasya_lms_backend.service.upload.UploadService;

@RestController
@RequestMapping("/sarvasya/upload")
@RequiredArgsConstructor
public class SarvasyaUploadController {

    private final UploadService uploadService;

    @PostMapping
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('tenant-manager')")
    public ResponseEntity<UploadVideoResponse> uploadVideo(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(uploadService.uploadVideo(file));
    }

    @GetMapping("/videos/{fileName:.+}")
    public ResponseEntity<Resource> getVideo(@PathVariable String fileName) {
        Resource resource = uploadService.getVideo(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}








