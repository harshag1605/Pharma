package com.pharma.prescription;

import java.util.List;
import java.util.UUID;

public interface PrescriptionRepository extends org.springframework.data.mongodb.repository.MongoRepository<Prescription, UUID> {
  List<Prescription> findByPatient_IdOrderByCreatedAtDesc(UUID patientId);
}
