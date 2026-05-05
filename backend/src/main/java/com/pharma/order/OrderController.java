package com.pharma.order;

import com.pharma.common.ApiResponse;
import com.pharma.order.OrderDtos.CreateOrderRequest;
import com.pharma.order.OrderDtos.PrescriptionOrderRequest;
import com.pharma.order.OrderDtos.SetOrderItemsRequest;
import com.pharma.order.OrderDtos.TransitionOrderRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService service;

  @PostMapping
  @PreAuthorize("hasRole('PATIENT')")
  ApiResponse<?> create(@Valid @RequestBody CreateOrderRequest request) {
    return ApiResponse.ok(service.create(request));
  }

  @PostMapping("/prescription-request")
  @PreAuthorize("hasRole('PATIENT')")
  ApiResponse<?> prescriptionRequest(@Valid @RequestBody PrescriptionOrderRequest request) {
    return ApiResponse.ok(service.createPrescriptionRequest(request));
  }

  @GetMapping("/mine")
  @PreAuthorize("hasRole('PATIENT')")
  ApiResponse<?> mine(Pageable pageable) {
    return ApiResponse.ok(service.mine(pageable));
  }

  @GetMapping
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> all(Pageable pageable) {
    return ApiResponse.ok(service.all(pageable));
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> transition(@PathVariable UUID id, @Valid @RequestBody TransitionOrderRequest request) {
    return ApiResponse.ok(service.transition(id, request));
  }

  @PutMapping("/{id}/items")
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> setItems(@PathVariable UUID id, @Valid @RequestBody SetOrderItemsRequest request) {
    return ApiResponse.ok(service.setItems(id, request));
  }
}
