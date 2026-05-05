package com.pharma.inventory;

import com.pharma.catalog.Medicine;
import com.pharma.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("inventory")
public class Inventory extends BaseEntity {
  @DBRef
  @Indexed(unique = true)
  private Medicine medicine;
  private int quantityAvailable;
  private int reorderLevel;
  @Version
  private Long version;
}
