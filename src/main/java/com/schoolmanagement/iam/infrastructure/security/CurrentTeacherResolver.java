package com.schoolmanagement.iam.infrastructure.security;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.teacher.domain.TeacherId;

@Component
public class CurrentTeacherResolver {
  private final UserRepository users;

  public CurrentTeacherResolver(UserRepository users) {
    this.users = users;
  }

  public Optional<TeacherId> resolve(Authentication authentication) {
    return users.findById(UserId.of(authentication.getName()))
        .map(User::personId)
        .filter(Objects::nonNull)
        .map(personId -> new TeacherId(personId.value()));
  }
}
