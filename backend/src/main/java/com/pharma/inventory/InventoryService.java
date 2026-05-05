package com.pharma.inventory;

import com.pharma.catalog.MedicineRepository;
import com.pharma.common.BusinessRuleException;
import com.pharma.common.NotFoundException;
import com.pharma.inventory.InventoryDtos.AdjustStockRequest;
import com.pharma.inventory.InventoryDtos.InventoryDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
  private final InventoryRepository inventory;
  private final MedicineRepository medicines;
  private final MongoTemplate mongoTemplate;

  public List<InventoryDto> list() {
    return inventory.findAll().stream().map(this::dto).toList();
  }

  public InventoryDto upsert(AdjustStockRequest request) {
    var medicine = medicines.findById(request.medicineId()).orElseThrow(() -> new NotFoundException("Medicine not found"));
    var row = inventory.findByMedicine_Id(request.medicineId()).orElseGet(Inventory::new);
    row.setMedicine(medicine);
    row.setQuantityAvailable(request.quantityAvailable());
    row.setReorderLevel(request.reorderLevel());
    return dto(inventory.save(row));
  }

  public void reserve(UUID medicineId, int quantity) {
    var query = new Query(Criteria.where("medicine.$id").is(medicineId).and("quantityAvailable").gte(quantity));
    var update = new Update().inc("quantityAvailable", -quantity);
    var row = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), Inventory.class);
    if (row == null) {
      var medicine = medicines.findById(medicineId).orElseThrow(() -> new NotFoundException("Medicine not found"));
      throw new BusinessRuleException("Insufficient stock for " + medicine.getBrandName());
    }
  }

  private InventoryDto dto(Inventory i) {
    return new InventoryDto(i.getId(), i.getMedicine().getId(), i.getMedicine().getBrandName(), i.getQuantityAvailable(), i.getReorderLevel());
  }
}
