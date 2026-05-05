package com.pharma.user;

import com.pharma.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("roles")
public class Role extends BaseEntity {
  @Indexed(unique = true)
  private RoleName name;
}
