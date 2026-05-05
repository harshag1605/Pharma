package com.pharma.auth;

import com.pharma.auth.AuthDtos.LoginRequest;
import com.pharma.auth.AuthDtos.RefreshRequest;
import com.pharma.auth.AuthDtos.RegisterRequest;
import com.pharma.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService service;

  @PostMapping("/register")
  ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
    return ApiResponse.ok(service.register(request));
  }

  @PostMapping("/login")
  ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.ok(service.login(request));
  }

  @PostMapping("/refresh")
  ApiResponse<?> refresh(@Valid @RequestBody RefreshRequest request) {
    return ApiResponse.ok(service.refresh(request.refreshToken()));
  }
}
