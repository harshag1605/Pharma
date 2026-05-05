package com.pharma.catalog;

import com.pharma.common.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("medicines")
public class Medicine extends BaseEntity {
  @Indexed
  private String brandName;
  @Indexed
  private String genericName;
  private String symptoms;
  private String dosageForm;
  private boolean prescriptionRequired;
  private BigDecimal price;
  private boolean active = true;
}
