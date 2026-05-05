package com.pharma.common;

import java.util.UUID;

public interface AuditLogRepository extends org.springframework.data.mongodb.repository.MongoRepository<AuditLog, UUID> {}
