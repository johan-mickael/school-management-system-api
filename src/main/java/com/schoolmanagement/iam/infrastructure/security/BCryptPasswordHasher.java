package com.schoolmanagement.iam.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.schoolmanagement.iam.domain.PasswordHash;
import com.schoolmanagement.iam.domain.PasswordHasher;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Override
  public PasswordHash hash(String rawPassword) {
    return new PasswordHash(encoder.encode(rawPassword));
  }

  @Override
  public boolean matches(String rawPassword, PasswordHash hash) {
    return encoder.matches(rawPassword, hash.value());
  }
}
