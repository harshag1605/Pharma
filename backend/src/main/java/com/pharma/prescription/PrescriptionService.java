package com.pharma.prescription;

import com.pharma.common.AuditService;
import com.pharma.common.NotFoundException;
import com.pharma.prescription.PrescriptionDtos.PrescriptionDto;
import com.pharma.prescription.PrescriptionDtos.SignedUrlResponse;
import com.pharma.security.CurrentUserService;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PrescriptionService {
  private final PrescriptionRepository prescriptions;
  private final CurrentUserService currentUser;
  private final StorageService storage;
  private final AuditService audit;

  public PrescriptionDto upload(MultipartFile file) throws IOException {
    var stored = storage.store(file);
    var p = new Prescription();
    p.setPatient(currentUser.user());
    p.setStorageKey(stored.key());
    p.setOriginalFilename(stored.originalFilename());
    p.setContentType(stored.contentType());
    p.setExtractedText("OCR pending");
    return dto(prescriptions.save(p));
  }

  public List<PrescriptionDto> mine() {
    return prescriptions.findByPatient_IdOrderByCreatedAtDesc(currentUser.user().getId()).stream().map(this::dto).toList();
  }

  public SignedUrlResponse signedUrl(UUID id) {
    var p = prescriptions.findById(id).orElseThrow(() -> new NotFoundException("Prescription not found"));
    audit.record("PRESCRIPTION_SIGNED_URL_CREATED", "Prescription", id, p.getOriginalFilename());
    return new SignedUrlResponse(storage.signedUrl(p.getStorageKey()), Instant.now().plusSeconds(900));
  }

  private PrescriptionDto dto(Prescription p) {
    return new PrescriptionDto(p.getId(), p.getOriginalFilename(), p.getContentType(), p.getCreatedAt(), p.getExtractedText());
  }
}
