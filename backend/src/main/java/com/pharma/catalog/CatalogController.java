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

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {
  private final CatalogService service;

  @GetMapping("/medicines")
  ApiResponse<?> search(@RequestParam(defaultValue = "") String q, Pageable pageable) {
    return ApiResponse.ok(service.search(q, pageable));
  }

  @GetMapping("/medicines/{id}/substitutes")
  ApiResponse<?> substitutes(@PathVariable UUID id) {
    return ApiResponse.ok(service.substitutes(id));
  }
}
