package com.pharma.catalog;

import com.pharma.catalog.CatalogDtos.MedicineDto;
import com.pharma.inventory.InventoryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// Marks this class as a Service component (business logic layer)
@Service

// Lombok annotation to auto-generate constructor for final fields (dependency injection)
@RequiredArgsConstructor
public class CatalogService {

  // Repository to access medicine data (catalog database)
  private final MedicineRepository medicines;

  // Repository to access inventory/stock data
  private final InventoryRepository inventory;

  // Method to search medicines based on query string with pagination support
  public Page<MedicineDto> search(String q, Pageable pageable) {

    // If query is null, replace with empty string to avoid errors
    // Calls repository search method and converts each Medicine entity to DTO
    return medicines.search(q == null ? "" : q, pageable).map(this::toDto);
  }

  // Method to fetch substitute medicines based on generic name
  public List<MedicineDto> substitutes(UUID medicineId) {

    // Fetch the medicine by ID, throws exception if not found
    var med = medicines.findById(medicineId).orElseThrow();

    // Find top 10 medicines with same generic name and active status
    // Filter out the original medicine itself
    // Convert each result to DTO
    return medicines.findTop10ByGenericNameIgnoreCaseAndActiveTrue(med.getGenericName()).stream()
        .filter(m -> !m.getId().equals(medicineId)) // exclude same medicine
        .map(this::toDto) // convert entity to DTO
        .toList(); // collect results as list
  }

  // Helper method to convert Medicine entity to MedicineDto
  private MedicineDto toDto(Medicine m) {

    // Fetch available quantity from inventory
    // If not found, default quantity = 0
    int qty = inventory.findByMedicine_Id(m.getId())
        .map(i -> i.getQuantityAvailable())
        .orElse(0);

    // Create and return DTO object with all required fields
    return new MedicineDto(
        m.getId(),
        m.getBrandName(),
        m.getGenericName(),
        m.getSymptoms(),
        m.getDosageForm(),
        m.isPrescriptionRequired(),
        m.getPrice(),
        qty
    );
  }
}