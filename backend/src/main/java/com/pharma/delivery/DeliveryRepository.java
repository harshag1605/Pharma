package com.pharma.delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends org.springframework.data.mongodb.repository.MongoRepository<Delivery, UUID> {
  List<Delivery> findByAgentIdOrderByCreatedAtDesc(UUID agentId);

  Optional<Delivery> findByOrderId(UUID orderId);
}
