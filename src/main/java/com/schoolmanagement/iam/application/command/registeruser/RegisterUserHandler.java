package com.schoolmanagement.iam.application.command.registeruser;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.iam.application.view.UserView;
import com.schoolmanagement.iam.domain.PasswordHasher;
import com.schoolmanagement.iam.domain.PersonId;
import com.schoolmanagement.iam.domain.Role;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;
import com.schoolmanagement.iam.domain.exception.InvalidPersonId;
import com.schoolmanagement.iam.domain.exception.UsernameAlreadyTaken;

@Service
public class RegisterUserHandler {
  private final UserRepository users;
  private final PasswordHasher passwordHasher;

  public RegisterUserHandler(UserRepository users, PasswordHasher passwordHasher) {
    this.users = users;
    this.passwordHasher = passwordHasher;
  }

  @Transactional
  public UserView handle(RegisterUserCommand command) {
    Username username = new Username(command.username());
    if (users.findByUsername(username).isPresent()) {
      throw new UsernameAlreadyTaken(username);
    }

    User user = User.register(
        UserId.generate(),
        username,
        passwordHasher.hash(command.password()),
        Role.of(command.role()),
        command.personId() == null || command.personId().isBlank()
            ? null
            : new PersonId(parsePersonId(command.personId())));

    users.save(user);

    return UserView.from(user);
  }

  private static UUID parsePersonId(String raw) {
    try {
      return UUID.fromString(raw);
    } catch (IllegalArgumentException malformed) {
      throw new InvalidPersonId(raw);
    }
  }
}
