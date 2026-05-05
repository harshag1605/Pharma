package com.pharma.delivery;

import com.pharma.common.ApiResponse;
import com.pharma.delivery.DeliveryDtos.AssignDeliveryRequest;
import com.pharma.delivery.DeliveryDtos.OtpDeliveryRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
  private final DeliveryService service;

  @PostMapping("/orders/{orderId}/assign")
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> assign(@PathVariable UUID orderId, @Valid @RequestBody AssignDeliveryRequest request) {
    return ApiResponse.ok(service.assign(orderId, request.agentId()));
  }

  @GetMapping("/mine")
  @PreAuthorize("hasRole('DELIVERY_AGENT')")
  ApiResponse<?> mine() {
    return ApiResponse.ok(service.mine());
  }

  @PatchMapping("/{id}/picked-up")
  @PreAuthorize("hasRole('DELIVERY_AGENT')")
  ApiResponse<?> pickedUp(@PathVariable UUID id) {
    return ApiResponse.ok(service.pickedUp(id));
  }

  @PatchMapping("/{id}/confirm")
  @PreAuthorize("hasRole('DELIVERY_AGENT')")
  ApiResponse<?> confirm(@PathVariable UUID id, @Valid @RequestBody OtpDeliveryRequest request) {
    return ApiResponse.ok(service.confirm(id, request.otp()));
  }
}
