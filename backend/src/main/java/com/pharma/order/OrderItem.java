package com.pharma.order;

import com.pharma.catalog.Medicine;
import com.pharma.common.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
public class OrderItem extends BaseEntity {
  @DBRef
  private Medicine medicine;
  private int quantity;
  private BigDecimal unitPrice;
}
