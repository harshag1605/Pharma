package com.pharma.catalog;

import com.pharma.common.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

// Lombok annotation to generate getter methods for all fields
@Getter

// Lombok annotation to generate setter methods for all fields
@Setter

// Marks this class as a MongoDB document (collection name: "medicines")
@Document("medicines")
public class Medicine extends BaseEntity {

  // Indexed field for faster search queries on brand name
  @Indexed
  private String brandName;

  // Indexed field for faster search queries on generic name
  @Indexed
  private String genericName;

  // Describes symptoms or conditions this medicine is used for
  private String symptoms;

  // Dosage form of the medicine (e.g., Tablet, Syrup, Capsule)
  private String dosageForm;

  // Indicates whether a valid prescription is required to purchase this medicine
  private boolean prescriptionRequired;

  // Price of the medicine (BigDecimal used for precise monetary values)
  private BigDecimal price;

  // Flag to indicate whether the medicine is active/available in the catalog
  // Default is true (active)
  private boolean active = true;
}