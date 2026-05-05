package com.pharma.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class InventoryDtos {
  public record InventoryDto(UUID id, UUID medicineId, String medicineName, int quantityAvailable, int reorderLevel) {}
  public record AdjustStockRequest(@NotNull UUID medicineId, @Min(0) int quantityAvailable, @Min(0) int reorderLevel) {}
}
