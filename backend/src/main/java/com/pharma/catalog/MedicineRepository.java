package com.pharma.catalog;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

// Repository interface for Medicine entity
// Extends MongoRepository to provide CRUD operations
public interface MedicineRepository extends org.springframework.data.mongodb.repository.MongoRepository<Medicine, UUID> {

  // Custom search query using MongoDB JSON query syntax
  // - Filters only active medicines (active: true)
  // - Performs case-insensitive regex search ($regex with 'i' option)
  // - Searches across multiple fields:
  //   brandName OR genericName OR symptoms
  // - Supports pagination using Pageable
  @Query("{ 'active': true, '$or': [ { 'brandName': { $regex: ?0, $options: 'i' } }, { 'genericName': { $regex: ?0, $options: 'i' } }, { 'symptoms': { $regex: ?0, $options: 'i' } } ] }")
  Page<Medicine> search(String q, Pageable pageable);

  // Derived query method (Spring Data automatically generates query)
  // - Finds top 10 medicines with matching generic name (case-insensitive)
  // - Filters only active medicines
  // Used for suggesting substitute medicines
  List<Medicine> findTop10ByGenericNameIgnoreCaseAndActiveTrue(String genericName);
}