package com.pharma.auth;

import com.pharma.auth.AuthDtos.AuthResponse;
import com.pharma.auth.AuthDtos.LoginRequest;
import com.pharma.auth.AuthDtos.RegisterRequest;
import com.pharma.common.BusinessRuleException;
import com.pharma.common.NotFoundException;
import com.pharma.security.CryptoService;
import com.pharma.security.JwtService;
import com.pharma.user.RoleRepository;
import com.pharma.user.User;
import com.pharma.user.UserRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwt;
  private final CryptoService crypto;

  public AuthResponse register(RegisterRequest request) {
    if (users.existsByEmailIgnoreCase(request.email())) throw new BusinessRuleException("Email is already registered");
    var role = roles.findByName(request.role()).orElseThrow(() -> new NotFoundException("Role not configured"));
    var user = new User();
    user.setFullName(request.fullName());
    user.setEmail(request.email().toLowerCase());
    user.setPasswordHash(encoder.encode(request.password()));
    user.setPhoneEncrypted(crypto.encrypt(request.phone()));
    user.getRoles().add(role);
    return response(users.save(user));
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    return response(users.findByEmailIgnoreCase(request.email()).orElseThrow());
  }

  public AuthResponse refresh(String refreshToken) {
    var email = jwt.subject(refreshToken);
    return response(users.findByEmailIgnoreCase(email).orElseThrow());
  }

  private AuthResponse response(User user) {
    return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(),
        user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()),
        jwt.accessToken(user), jwt.refreshToken(user));
  }
}
