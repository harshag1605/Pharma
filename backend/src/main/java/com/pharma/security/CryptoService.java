package com.pharma.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {
  public String encrypt(String value) {
    return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }
  public String decrypt(String value) {
    return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
  }
}
