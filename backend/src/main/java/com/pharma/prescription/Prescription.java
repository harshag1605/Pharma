package com.pharma.prescription;

import com.pharma.common.BaseEntity;
import com.pharma.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("prescriptions")
public class Prescription extends BaseEntity {
  @DBRef
  @Indexed
  private User patient;
  private String storageKey;
  private String originalFilename;
  private String contentType;
  private String extractedText;
}
