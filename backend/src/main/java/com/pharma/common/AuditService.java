package com.pharma.common;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
  private final AuditLogRepository repository;

  public void record(String action, String resourceType, UUID resourceId, String details) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    var log = new AuditLog();
    log.setActorEmail(auth == null ? "system" : auth.getName());
    log.setAction(action);
    log.setResourceType(resourceType);
    log.setResourceId(resourceId);
    log.setDetails(details);
    repository.save(log);
  }
}
