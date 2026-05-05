package com.pharma.delivery;

import com.pharma.common.BaseEntity;
import com.pharma.order.PharmacyOrder;
import com.pharma.user.User;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("delivery")
public class Delivery extends BaseEntity {
  @Indexed(unique = true)
  private UUID orderId;
  @Indexed
  private UUID agentId;
  @DBRef
  private PharmacyOrder order;
  @DBRef
  private User agent;
  private DeliveryStatus status = DeliveryStatus.ASSIGNED;
  private String otpHash;
  private String otpCode;
  private Instant deliveredAt;
}
