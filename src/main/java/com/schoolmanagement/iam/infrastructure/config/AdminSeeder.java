package com.schoolmanagement.iam.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.schoolmanagement.iam.domain.PasswordHasher;
import com.schoolmanagement.iam.domain.Role;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;

@Component
public class AdminSeeder implements CommandLineRunner {
  private final UserRepository users;
  private final PasswordHasher passwordHasher;
  private final boolean enabled;
  private final String username;
  private final String password;

  public AdminSeeder(
      UserRepository users,
      PasswordHasher passwordHasher,
      @Value("${app.security.seed-admin.enabled:false}") boolean enabled,
      @Value("${app.security.seed-admin.username:admin}") String username,
      @Value("${app.security.seed-admin.password:}") String password) {
    this.users = users;
    this.passwordHasher = passwordHasher;
    this.enabled = enabled;
    this.username = username;
    this.password = password;
  }

  @Override
  public void run(String... args) {
    if (!enabled) {
      return;
    }
    Username adminUsername = new Username(username);
    if (users.findByUsername(adminUsername).isPresent()) {
      return;
    }
    User admin = User.register(
        UserId.generate(), adminUsername, passwordHasher.hash(password), Role.ADMIN, null);
    users.save(admin);
  }
}
