package com.pharma.prescription;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
  private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "application/pdf");
  private final Path root;

  public StorageService(@Value("${app.storage.prescription-dir}") String dir) throws IOException {
    this.root = Path.of(dir).toAbsolutePath().normalize();
    Files.createDirectories(root);
  }

  public StoredFile store(MultipartFile file) throws IOException {
    if (!ALLOWED.contains(file.getContentType())) throw new IllegalArgumentException("Only JPG, PNG, and PDF prescriptions are allowed");
    String ext = switch (file.getContentType()) {
      case "image/jpeg" -> ".jpg";
      case "image/png" -> ".png";
      default -> ".pdf";
    };
    String key = UUID.randomUUID() + ext;
    Files.copy(file.getInputStream(), root.resolve(key));
    return new StoredFile(key, file.getOriginalFilename(), file.getContentType());
  }

  public String signedUrl(String key) {
    String encoded = URLEncoder.encode(key, StandardCharsets.UTF_8);
    return "/api/prescriptions/files/" + encoded + "?signature=local-dev&expires=" + Instant.now().plusSeconds(900).toEpochMilli();
  }

  public Path path(String key) {
    return root.resolve(key).normalize();
  }

  public record StoredFile(String key, String originalFilename, String contentType) {}
}
