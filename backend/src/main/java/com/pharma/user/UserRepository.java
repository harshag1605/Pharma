package com.pharma.user;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends org.springframework.data.mongodb.repository.MongoRepository<User, UUID> {
  Optional<User> findByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCase(String email);
}
