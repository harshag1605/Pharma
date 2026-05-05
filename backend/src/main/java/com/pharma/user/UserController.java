package com.pharma.user;

import com.pharma.common.ApiResponse;
import com.pharma.user.UserDtos.UserDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserRepository users;

  @GetMapping
  @PreAuthorize("hasRole('PHARMACIST')")
  ApiResponse<List<UserDto>> list(@RequestParam(required = false) RoleName role) {
    var data = users.findAll().stream()
        .filter(u -> role == null || u.getRoles().stream().anyMatch(r -> r.getName() == role))
        .map(u -> new UserDto(u.getId(), u.getFullName(), u.getEmail(), u.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet())))
        .toList();
    return ApiResponse.ok(data);
  }
}
