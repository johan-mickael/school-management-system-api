package com.schoolmanagement.iam.infrastructure.persistence;

import java.util.UUID;

import com.schoolmanagement.iam.domain.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(name = "person_id")
  private UUID personId;

  @Column(nullable = false)
  private boolean enabled;

  protected UserEntity() {}

  public UserEntity(
      UUID id,
      String username,
      String passwordHash,
      Role role,
      UUID personId,
      boolean enabled) {
    this.id = id;
    this.username = username;
    this.passwordHash = passwordHash;
    this.role = role;
    this.personId = personId;
    this.enabled = enabled;
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public UUID getPersonId() {
    return personId;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
