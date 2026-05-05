package com.pharma.user;

import java.util.Set;
import java.util.UUID;

public class UserDtos {
  public record UserDto(UUID id, String fullName, String email, Set<RoleName> roles) {}
}
