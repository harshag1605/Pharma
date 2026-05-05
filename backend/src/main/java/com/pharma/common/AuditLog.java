package com.pharma.common;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("audit_logs")
public class AuditLog extends BaseEntity {
  private String actorEmail;
  private String action;
  private String resourceType;
  private UUID resourceId;
  private String details;
}
