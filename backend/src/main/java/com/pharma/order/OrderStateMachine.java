package com.pharma.order;

import com.pharma.common.BusinessRuleException;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OrderStateMachine {
  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
      OrderStatus.PENDING_VERIFICATION, Set.of(OrderStatus.APPROVED, OrderStatus.REJECTED),
      OrderStatus.APPROVED, Set.of(OrderStatus.PROCESSING),
      OrderStatus.PROCESSING, Set.of(OrderStatus.OUT_FOR_DELIVERY),
      OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED),
      OrderStatus.REJECTED, Set.of(),
      OrderStatus.DELIVERED, Set.of()
  );

  public void validate(OrderStatus from, OrderStatus to) {
    if (!ALLOWED.getOrDefault(from, Set.of()).contains(to)) {
      throw new BusinessRuleException("Invalid order transition from " + from + " to " + to);
    }
  }
}
