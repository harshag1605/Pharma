package com.pharma.prescription;

import java.time.Instant;
import java.util.UUID;

public class PrescriptionDtos {
  public record PrescriptionDto(UUID id, String originalFilename, String contentType, Instant createdAt, String extractedText) {}
  public record SignedUrlResponse(String url, Instant expiresAt) {}
}
