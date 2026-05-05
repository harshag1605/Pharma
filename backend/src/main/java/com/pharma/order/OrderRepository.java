package com.pharma.order;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends org.springframework.data.mongodb.repository.MongoRepository<PharmacyOrder, UUID> {
  Page<PharmacyOrder> findByPatientId(UUID patientId, Pageable pageable);

  Page<PharmacyOrder> findAll(Pageable pageable);
}
