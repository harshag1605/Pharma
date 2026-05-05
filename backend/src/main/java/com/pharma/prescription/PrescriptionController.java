package com.pharma.prescription;

import com.pharma.common.ApiResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
  private final PrescriptionService service;
  private final StorageService storage;

  @PostMapping
  @PreAuthorize("hasRole('PATIENT')")
  ApiResponse<?> upload(@RequestParam MultipartFile file) throws IOException {
    return ApiResponse.ok(service.upload(file));
  }

  @GetMapping("/mine")
  @PreAuthorize("hasRole('PATIENT')")
  ApiResponse<?> mine() {
    return ApiResponse.ok(service.mine());
  }

  @GetMapping("/{id}/signed-url")
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> signedUrl(@PathVariable UUID id) {
    return ApiResponse.ok(service.signedUrl(id));
  }

  @GetMapping("/files/{key}")
  @PreAuthorize("hasRole('PHARMACIST')")
  ResponseEntity<?> file(@PathVariable String key) throws IOException {
    var path = storage.path(key);
    return ResponseEntity.ok().header("Content-Type", Files.probeContentType(path)).body(new FileSystemResource(path));
  }
}
