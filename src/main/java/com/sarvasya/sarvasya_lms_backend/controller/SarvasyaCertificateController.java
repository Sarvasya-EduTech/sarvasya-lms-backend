package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaCertificate;
import com.sarvasya.sarvasya_lms_backend.service.SarvasyaCertificateService;
import com.sarvasya.sarvasya_lms_backend.service.StudentIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/sarvasya/{tenantid}/certificates")
@RequiredArgsConstructor
public class SarvasyaCertificateController {

    private final SarvasyaCertificateService service;
    private final StudentIdResolver studentIdResolver;

    @PostMapping
    public ResponseEntity<SarvasyaCertificate> create(@RequestBody Map<String, Object> payload) {
        final UUID studentId = studentIdResolver.resolveStudentId((String) payload.get("studentId"));
        final UUID courseId = UUID.fromString(String.valueOf(payload.get("courseId")));
        final String certificateUrl = payload.get("certificateUrl") != null ? payload.get("certificateUrl").toString() : null;
        final LocalDateTime issuedAt = parseDateTime(payload.get("issuedAt"));

        SarvasyaCertificate cert = SarvasyaCertificate.builder()
                .studentId(studentId)
                .courseId(courseId)
                .certificateUrl(certificateUrl)
                .issuedAt(issuedAt)
                .build();
        return ResponseEntity.ok(service.createOrUpdate(cert));
    }

    @GetMapping("/by-student-course")
    public ResponseEntity<SarvasyaCertificate> byStudentCourse(
            @RequestParam(required = false) String studentId,
            @RequestParam String courseId
    ) {
        final UUID resolvedStudentId = studentIdResolver.resolveStudentId(studentId);
        final UUID resolvedCourseId = UUID.fromString(courseId);
        final Optional<SarvasyaCertificate> cert = service.findByStudentAndCourse(resolvedStudentId, resolvedCourseId);
        return cert.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaCertificate>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaCertificate> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable UUID id) {
        final SarvasyaCertificate cert = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Certificate not found"));

        final String url = cert.getCertificateUrl();
        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(NOT_FOUND, "certificateUrl not set for this certificate");
        }

        // If we stored a data URL (e.g. data:application/pdf;base64,....), stream it as a real file.
        if (url.startsWith("data:")) {
            final DownloadPayload payload = parseDataUrl(url);
            final String filename = "certificate-" + cert.getId() + payload.defaultExtension();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(payload.mediaType())
                    .body(payload.bytes());
        }

        // Otherwise treat it as an HTTP(S) URL and redirect so the browser downloads it.
        // (Frontends can also just use the returned Location to open a new tab.)
        return ResponseEntity.status(302)
                .location(URI.create(url))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaCertificate> update(@PathVariable UUID id, @RequestBody SarvasyaCertificate payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    private record DownloadPayload(byte[] bytes, MediaType mediaType) {
        String defaultExtension() {
            if (MediaType.APPLICATION_PDF.equals(mediaType)) return ".pdf";
            if (MediaType.IMAGE_PNG.equals(mediaType)) return ".png";
            if (MediaType.IMAGE_JPEG.equals(mediaType)) return ".jpg";
            return "";
        }
    }

    private static DownloadPayload parseDataUrl(String dataUrl) {
        // Format: data:[<mediatype>][;base64],<data>
        final int comma = dataUrl.indexOf(',');
        if (comma < 0) {
            throw new ResponseStatusException(NOT_FOUND, "Invalid data URL");
        }

        final String meta = dataUrl.substring(5, comma); // after "data:"
        final String dataPart = dataUrl.substring(comma + 1);

        final boolean base64 = meta.contains(";base64");
        final String mediaTypeRaw = meta.replace(";base64", "").trim();
        final MediaType mediaType = (mediaTypeRaw.isBlank())
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(mediaTypeRaw);

        final byte[] bytes = base64
                ? Base64.getDecoder().decode(dataPart)
                : dataPart.getBytes(StandardCharsets.UTF_8);

        return new DownloadPayload(bytes, mediaType);
    }

    private static LocalDateTime parseDateTime(Object v) {
        if (v == null) return null;
        try { return LocalDateTime.parse(v.toString()); } catch (Exception e) { return null; }
    }
}
