package com.pharma.inventory;

import com.pharma.common.ApiResponse;
import com.pharma.inventory.InventoryDtos.AdjustStockRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
  private final InventoryService service;

  @GetMapping
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> list() {
    return ApiResponse.ok(service.list());
  }

  @PostMapping
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> upsert(@Valid @RequestBody AdjustStockRequest request) {
    return ApiResponse.ok(service.upsert(request));
  }
}
