package com.pharma.user;

import com.pharma.common.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("users")
public class User extends BaseEntity {
  private String fullName;
  @Indexed(unique = true)
  private String email;
  private String passwordHash;
  private String phoneEncrypted;
  private boolean active = true;

  @DBRef
  private Set<Role> roles = new HashSet<>();
}
