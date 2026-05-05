package com.pharma.auth;

import com.pharma.auth.AuthDtos.LoginRequest;
import com.pharma.auth.AuthDtos.RefreshRequest;
import com.pharma.auth.AuthDtos.RegisterRequest;
import com.pharma.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling authentication-related operations
 * such as user registration, login, and token refresh.
 */
@RestController
@RequestMapping("/api/auth") // Base URL for all auth APIs
@RequiredArgsConstructor // Generates constructor for final fields (AuthService)
public class AuthController {

  // Service layer dependency to handle business logic
  private final AuthService service;

  /**
   * Register a new user
   *
   * @param request contains user registration details (validated)
   * @return ApiResponse containing registered user or token details
   */
  @PostMapping("/register")
  ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
    // Calls service layer to register user and wraps response
    return ApiResponse.ok(service.register(request));
  }

  /**
   * Authenticate user and generate access/refresh tokens
   *
   * @param request contains login credentials (validated)
   * @return ApiResponse containing authentication tokens or user info
   */
  @PostMapping("/login")
  ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
    // Calls service layer to authenticate user
    return ApiResponse.ok(service.login(request));
  }

  /**
   * Refresh access token using a valid refresh token
   *
   * @param request contains refresh token (validated)
   * @return ApiResponse containing new access token
   */
  @PostMapping("/refresh")
  ApiResponse<?> refresh(@Valid @RequestBody RefreshRequest request) {
    // Extract refresh token and request new access token
    return ApiResponse.ok(service.refresh(request.refreshToken()));
  }
}