package com.pharma.auth;

import com.pharma.user.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public class AuthDtos {
  public record RegisterRequest(@NotBlank String fullName, @Email String email, @NotBlank String password,
                                @NotBlank String phone, @NotNull RoleName role) {}
  public record LoginRequest(@Email String email, @NotBlank String password) {}
  public record RefreshRequest(@NotBlank String refreshToken) {}
  public record AuthResponse(UUID userId, String fullName, String email, Set<RoleName> roles, String accessToken, String refreshToken) {}
}
