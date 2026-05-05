package com.pharma.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderDtos {
  public record CreateOrderRequest(UUID prescriptionId, @NotBlank String deliveryAddress, @NotEmpty List<CreateOrderItem> items) {}
  public record PrescriptionOrderRequest(@NotNull UUID prescriptionId, @NotBlank String deliveryAddress) {}
  public record SetOrderItemsRequest(@NotEmpty List<CreateOrderItem> items) {}
  public record CreateOrderItem(@NotNull UUID medicineId, @Min(1) int quantity) {}
  public record TransitionOrderRequest(@NotNull OrderStatus status, String reason) {}
  public record OrderItemDto(UUID medicineId, String brandName, String genericName, int quantity, BigDecimal unitPrice) {}
  public record OrderDto(UUID id, String patientName, UUID prescriptionId, OrderStatus status, BigDecimal totalAmount,
                         String rejectionReason, String deliveryAddress, Instant createdAt, List<OrderItemDto> items,
                         String deliveryAgent, String deliveryStatus, String deliveryOtp) {}
}
