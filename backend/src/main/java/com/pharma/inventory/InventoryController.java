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

// Marks this class as a REST controller (handles HTTP requests and returns JSON responses)
@RestController

// Base URL mapping for all inventory-related APIs
@RequestMapping("/api/inventory")

// Generates constructor for all final fields (InventoryService injection)
@RequiredArgsConstructor
public class InventoryController {

  // Service layer dependency to handle business logic for inventory
  private final InventoryService service;

  // Handles GET requests to fetch inventory list
  @GetMapping

  // Ensures only users with role PHARMACIST can access this endpoint
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> list() {

    // Calls service layer to retrieve inventory data and wraps it in a standard API response
    return ApiResponse.ok(service.list());
  }

  // Handles POST requests to add/update inventory stock
  @PostMapping

  // Restricts access to users with PHARMACIST role
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<?> upsert(@Valid @RequestBody AdjustStockRequest request) {

    // Validates incoming request body and passes it to service layer for processing
    return ApiResponse.ok(service.upsert(request));
  }
}