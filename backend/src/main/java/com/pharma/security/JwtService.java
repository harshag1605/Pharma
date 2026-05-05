package com.pharma.security;

import com.pharma.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  private final Duration accessTtl;
  private final Duration refreshTtl;

  public JwtService(@Value("${app.security.jwt-secret}") String secret,
                    @Value("${app.security.access-token-minutes}") long accessMinutes,
                    @Value("${app.security.refresh-token-days}") long refreshDays) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTtl = Duration.ofMinutes(accessMinutes);
    this.refreshTtl = Duration.ofDays(refreshDays);
  }

  public String accessToken(User user) {
    return token(user, accessTtl, Map.of("typ", "access", "roles", user.getRoles().stream().map(r -> r.getName().name()).toList()));
  }

  public String refreshToken(User user) {
    return token(user, refreshTtl, Map.of("typ", "refresh"));
  }

  public String subject(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
  }

  private String token(User user, Duration ttl, Map<String, Object> claims) {
    var now = Instant.now();
    return Jwts.builder()
        .subject(user.getEmail())
        .claims(claims)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(ttl)))
        .signWith(key)
        .compact();
  }
}
