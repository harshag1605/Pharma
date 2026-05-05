package com.pharma.user;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends org.springframework.data.mongodb.repository.MongoRepository<Role, UUID> {
  Optional<Role> findByName(RoleName name);
}
