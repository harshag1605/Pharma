package com.pharma.order;

import com.pharma.common.BaseEntity;
import com.pharma.prescription.Prescription;
import com.pharma.user.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("orders")
public class PharmacyOrder extends BaseEntity {
  @Indexed
  private UUID patientId;
  @DBRef
  private User patient;
  @DBRef
  private Prescription prescription;
  @Indexed
  private OrderStatus status = OrderStatus.PENDING_VERIFICATION;
  private BigDecimal totalAmount = BigDecimal.ZERO;
  private String rejectionReason;
  private String deliveryAddress;

  private List<OrderItem> items = new ArrayList<>();

  @DBRef
  private com.pharma.delivery.Delivery delivery;
}
