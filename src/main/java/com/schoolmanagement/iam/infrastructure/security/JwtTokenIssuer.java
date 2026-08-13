package com.schoolmanagement.iam.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.schoolmanagement.iam.domain.TokenIssuer;
import com.schoolmanagement.iam.domain.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenIssuer implements TokenIssuer {
  private final SecretKey key;
  private final long ttlSeconds;
  private final Clock clock;

  public JwtTokenIssuer(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.ttl-seconds}") long ttlSeconds,
      Clock clock) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.ttlSeconds = ttlSeconds;
    this.clock = clock;
  }

  @Override
  public String issue(User user) {
    Instant now = Instant.now(clock);
    return Jwts.builder()
        .subject(user.id().toString())
        .claim("username", user.username().value())
        .claim("role", user.role().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(ttlSeconds)))
        .signWith(key)
        .compact();
  }
}
