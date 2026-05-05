package com.pharma.catalog;

import java.math.BigDecimal;
import java.util.UUID;

// DTO (Data Transfer Object) class used to transfer medicine data
// between backend and frontend without exposing internal entity structure
public class CatalogDtos {

  // Java Record used for immutable data representation of a Medicine
  // Automatically provides constructor, getters, equals, hashCode, and toString
  public record MedicineDto(
      
      // Unique identifier for the medicine (UUID ensures global uniqueness)
      UUID id,
      
      // Brand name of the medicine (e.g., Crocin, Dolo)
      String brandName,
      
      // Generic name of the medicine (e.g., Paracetamol)
      String genericName,
      
      // Symptoms or conditions the medicine is used for
      String symptoms,
      
      // Dosage form (e.g., Tablet, Syrup, Capsule)
      String dosageForm,
      
      // Indicates whether a prescription is required to purchase
      boolean prescriptionRequired,
      
      // Price of the medicine (BigDecimal used for precision in currency)
      BigDecimal price,
      
      // Available stock quantity of the medicine
      int availableQuantity
  ) {}
}