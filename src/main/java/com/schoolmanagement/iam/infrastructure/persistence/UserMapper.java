package com.schoolmanagement.iam.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.iam.domain.PasswordHash;
import com.schoolmanagement.iam.domain.PersonId;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.Username;

@Component
public class UserMapper {

  public UserEntity toEntity(User u) {
    return new UserEntity(
        u.id().value(),
        u.username().value(),
        u.passwordHash().value(),
        u.role(),
        u.personId() == null ? null : u.personId().value(),
        u.enabled());
  }

  public User toDomain(UserEntity e) {
    return User.reconstitute(
        new UserId(e.getId()),
        new Username(e.getUsername()),
        new PasswordHash(e.getPasswordHash()),
        e.getRole(),
        e.getPersonId() == null ? null : new PersonId(e.getPersonId()),
        e.isEnabled());
  }
}
