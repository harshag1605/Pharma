package com.pharma.catalog;

import com.pharma.common.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Marks this class as a REST controller (handles HTTP requests and returns JSON responses)
@RestController

// Base URL for all endpoints in this controller
@RequestMapping("/api/catalog")

// Lombok annotation to generate constructor for final fields (dependency injection)
@RequiredArgsConstructor
public class CatalogController {

  // Service layer dependency to handle business logic
  private final CatalogService service;

  // Endpoint to search medicines
  // Example: GET /api/catalog/medicines?q=paracetamol&page=0&size=10
  @GetMapping("/medicines")
  ApiResponse<?> search(
      // Query parameter for searching medicines (default is empty string → returns all)
      @RequestParam(defaultValue = "") String q,

      // Pageable object for pagination (page number, size, sorting)
      Pageable pageable
  ) {
    // Calls service layer to perform search and wraps result in ApiResponse
    return ApiResponse.ok(service.search(q, pageable));
  }

  // Endpoint to get substitute medicines for a given medicine ID
  // Example: GET /api/catalog/medicines/{id}/substitutes
  @GetMapping("/medicines/{id}/substitutes")
  ApiResponse<?> substitutes(
      // Path variable representing medicine UUID
      @PathVariable UUID id
  ) {
    // Calls service layer to fetch substitute medicines
    return ApiResponse.ok(service.substitutes(id));
  }
}