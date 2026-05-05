package com.pharma.catalog;

import com.pharma.catalog.CatalogDtos.MedicineDto;
import com.pharma.inventory.InventoryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogService {
  private final MedicineRepository medicines;
  private final InventoryRepository inventory;

  public Page<MedicineDto> search(String q, Pageable pageable) {
    return medicines.search(q == null ? "" : q, pageable).map(this::toDto);
  }

  public List<MedicineDto> substitutes(UUID medicineId) {
    var med = medicines.findById(medicineId).orElseThrow();
    return medicines.findTop10ByGenericNameIgnoreCaseAndActiveTrue(med.getGenericName()).stream()
        .filter(m -> !m.getId().equals(medicineId))
        .map(this::toDto)
        .toList();
  }

  private MedicineDto toDto(Medicine m) {
    int qty = inventory.findByMedicine_Id(m.getId()).map(i -> i.getQuantityAvailable()).orElse(0);
    return new MedicineDto(m.getId(), m.getBrandName(), m.getGenericName(), m.getSymptoms(), m.getDosageForm(),
        m.isPrescriptionRequired(), m.getPrice(), qty);
  }
}
