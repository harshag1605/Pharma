package com.pharma.inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends org.springframework.data.mongodb.repository.MongoRepository<Inventory, UUID> {
  Optional<Inventory> findByMedicine_Id(UUID medicineId);
}
