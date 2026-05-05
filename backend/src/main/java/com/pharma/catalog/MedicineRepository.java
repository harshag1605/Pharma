package com.pharma.catalog;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

public interface MedicineRepository extends org.springframework.data.mongodb.repository.MongoRepository<Medicine, UUID> {
  @Query("{ 'active': true, '$or': [ { 'brandName': { $regex: ?0, $options: 'i' } }, { 'genericName': { $regex: ?0, $options: 'i' } }, { 'symptoms': { $regex: ?0, $options: 'i' } } ] }")
  Page<Medicine> search(String q, Pageable pageable);

  List<Medicine> findTop10ByGenericNameIgnoreCaseAndActiveTrue(String genericName);
}
