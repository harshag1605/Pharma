package com.pharma.config;

import com.pharma.common.BaseEntity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Configuration
@EnableMongoAuditing
public class MongoConfig {}

@Component
class BaseEntityMongoListener extends AbstractMongoEventListener<BaseEntity> {
  @Override
  public void onBeforeConvert(BeforeConvertEvent<BaseEntity> event) {
    var entity = event.getSource();
    var now = Instant.now();
    if (entity.getId() == null) entity.setId(UUID.randomUUID());
    if (entity.getCreatedAt() == null) entity.setCreatedAt(now);
    entity.setUpdatedAt(now);
  }
}
