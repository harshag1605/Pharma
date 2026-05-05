package com.pharma.catalog;

import java.math.BigDecimal;
import java.util.UUID;

public class CatalogDtos {
  public record MedicineDto(UUID id, String brandName, String genericName, String symptoms, String dosageForm,
                            boolean prescriptionRequired, BigDecimal price, int availableQuantity) {}
}
