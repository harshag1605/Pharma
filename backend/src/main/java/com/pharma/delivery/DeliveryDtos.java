package com.pharma.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class DeliveryDtos {
  public record AssignDeliveryRequest(@NotNull UUID agentId) {}
  public record OtpDeliveryRequest(@NotBlank String otp) {}
  public record DeliveryDto(UUID id, UUID orderId, String patientName, String address, String agentName, DeliveryStatus status) {}
}
