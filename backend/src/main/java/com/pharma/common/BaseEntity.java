package com.pharma.common;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public abstract class BaseEntity {
  @Id
  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
}
