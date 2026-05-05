package com.pharma.security;

import com.pharma.common.NotFoundException;
import com.pharma.user.User;
import com.pharma.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
  private final UserRepository users;

  public User user() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return users.findByEmailIgnoreCase(email).orElseThrow(() -> new NotFoundException("Current user not found"));
  }
}
