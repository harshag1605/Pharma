package com.pharma.delivery;

import com.pharma.common.BusinessRuleException;
import com.pharma.common.NotFoundException;
import com.pharma.delivery.DeliveryDtos.DeliveryDto;
import com.pharma.notification.NotificationService;
import com.pharma.order.OrderRepository;
import com.pharma.order.OrderStatus;
import com.pharma.order.OrderStateMachine;
import com.pharma.security.CurrentUserService;
import com.pharma.user.RoleName;
import com.pharma.user.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {
  private final DeliveryRepository deliveries;
  private final OrderRepository orders;
  private final UserRepository users;
  private final CurrentUserService currentUser;
  private final PasswordEncoder encoder;
  private final OrderStateMachine stateMachine;
  private final NotificationService notifications;

  public DeliveryDto assign(UUID orderId, UUID agentId) {
    var order = orders.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
    if (order.getStatus() != OrderStatus.PROCESSING) throw new BusinessRuleException("Order must be PROCESSING before delivery assignment");
    var agent = users.findById(agentId).orElseThrow(() -> new NotFoundException("Delivery agent not found"));
    boolean isAgent = agent.getRoles().stream().anyMatch(r -> r.getName() == RoleName.DELIVERY_AGENT);
    if (!isAgent) throw new BusinessRuleException("Selected user is not a delivery agent");
    var delivery = deliveries.findByOrderId(orderId).orElseGet(Delivery::new);
    delivery.setOrderId(orderId);
    delivery.setAgentId(agentId);
    delivery.setOrder(order);
    delivery.setAgent(agent);
    delivery.setStatus(DeliveryStatus.ASSIGNED);
    String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
    delivery.setOtpHash(encoder.encode(otp));
    delivery.setOtpCode(otp);
    stateMachine.validate(order.getStatus(), OrderStatus.OUT_FOR_DELIVERY);
    order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
    var savedDelivery = deliveries.save(delivery);
    order.setDelivery(savedDelivery);
    orders.save(order);
    notifications.orderUpdate(order, "Delivery assigned. OTP sent to patient: " + otp);
    return dto(savedDelivery);
  }

  public List<DeliveryDto> mine() {
    var agentId = currentUser.user().getId();
    var assigned = deliveries.findByAgentIdOrderByCreatedAtDesc(agentId);
    if (assigned.isEmpty()) {
      assigned = deliveries.findAll().stream()
          .filter(delivery -> delivery.getAgent() != null && agentId.equals(delivery.getAgent().getId()))
          .toList();
    }
    return assigned.stream().map(this::dto).toList();
  }

  public DeliveryDto pickedUp(UUID deliveryId) {
    var d = deliveries.findById(deliveryId).orElseThrow(() -> new NotFoundException("Delivery not found"));
    d.setStatus(DeliveryStatus.PICKED_UP);
    return dto(deliveries.save(d));
  }

  public DeliveryDto confirm(UUID deliveryId, String otp) {
    var d = deliveries.findById(deliveryId).orElseThrow(() -> new NotFoundException("Delivery not found"));
    if (!encoder.matches(otp, d.getOtpHash())) throw new BusinessRuleException("Invalid delivery OTP");
    d.setStatus(DeliveryStatus.DELIVERED);
    d.setDeliveredAt(Instant.now());
    stateMachine.validate(d.getOrder().getStatus(), OrderStatus.DELIVERED);
    d.getOrder().setStatus(OrderStatus.DELIVERED);
    orders.save(d.getOrder());
    notifications.orderUpdate(d.getOrder(), "Order delivered");
    return dto(deliveries.save(d));
  }

  private DeliveryDto dto(Delivery d) {
    return new DeliveryDto(d.getId(), d.getOrder().getId(), d.getOrder().getPatient().getFullName(),
        d.getOrder().getDeliveryAddress(), d.getAgent().getFullName(), d.getStatus());
  }
}
