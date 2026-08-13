package com.schoolmanagement.iam.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.iam.application.command.login.LoginCommand;
import com.schoolmanagement.iam.application.command.login.LoginHandler;
import com.schoolmanagement.iam.application.command.registeruser.RegisterUserCommand;
import com.schoolmanagement.iam.application.command.registeruser.RegisterUserHandler;
import com.schoolmanagement.iam.application.view.UserView;
import com.schoolmanagement.iam.domain.PasswordHash;
import com.schoolmanagement.iam.domain.PasswordHasher;
import com.schoolmanagement.iam.domain.TokenIssuer;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;
import com.schoolmanagement.iam.domain.exception.InvalidCredentials;
import com.schoolmanagement.iam.domain.exception.InvalidRole;
import com.schoolmanagement.iam.domain.exception.UsernameAlreadyTaken;

class AuthenticationHandlerTest {
  static class InMemoryUsers implements UserRepository {
    final Map<String, User> db = new HashMap<>();

    public void save(User u) {
      db.put(u.username().value(), u);
    }

    public Optional<User> findByUsername(Username username) {
      return Optional.ofNullable(db.get(username.value()));
    }

    public Optional<User> findById(UserId id) {
      return db.values().stream().filter(u -> u.id().equals(id)).findFirst();
    }
  }

  static class FakePasswordHasher implements PasswordHasher {
    public PasswordHash hash(String rawPassword) {
      return new PasswordHash("hashed:" + rawPassword);
    }

    public boolean matches(String rawPassword, PasswordHash hash) {
      return hash.value().equals("hashed:" + rawPassword);
    }
  }

  static class FakeTokenIssuer implements TokenIssuer {
    public String issue(User user) {
      return "token-for-" + user.username().value();
    }
  }

  private final InMemoryUsers users = new InMemoryUsers();
  private final FakePasswordHasher passwordHasher = new FakePasswordHasher();
  private final RegisterUserHandler registerHandler = new RegisterUserHandler(users, passwordHasher);
  private final LoginHandler loginHandler = new LoginHandler(users, passwordHasher, new FakeTokenIssuer());

  @Test
  void registers_a_user_with_a_hashed_password() {
    UserView view = registerHandler.handle(
        new RegisterUserCommand("ada", "secret123", "ADMIN", null));

    assertThat(view.role()).isEqualTo("ADMIN");
    assertThat(users.db.get("ada").passwordHash().value()).isEqualTo("hashed:secret123");
  }

  @Test
  void rejects_a_duplicate_username() {
    registerHandler.handle(new RegisterUserCommand("ada", "secret123", "ADMIN", null));

    assertThatThrownBy(() -> registerHandler.handle(
        new RegisterUserCommand("ada", "different", "TEACHER", null)))
            .isInstanceOf(UsernameAlreadyTaken.class);
  }

  @Test
  void rejects_an_invalid_role() {
    assertThatThrownBy(() -> registerHandler.handle(
        new RegisterUserCommand("ada", "secret123", "SUPERUSER", null)))
            .isInstanceOf(InvalidRole.class);
  }

  @Test
  void logs_in_with_correct_credentials() {
    registerHandler.handle(new RegisterUserCommand("ada", "secret123", "ADMIN", null));

    var view = loginHandler.handle(new LoginCommand("ada", "secret123"));

    assertThat(view.token()).isEqualTo("token-for-ada");
  }

  @Test
  void rejects_login_for_unknown_username() {
    assertThatThrownBy(() -> loginHandler.handle(new LoginCommand("ghost", "whatever")))
        .isInstanceOf(InvalidCredentials.class);
  }

  @Test
  void rejects_login_with_wrong_password() {
    registerHandler.handle(new RegisterUserCommand("ada", "secret123", "ADMIN", null));

    assertThatThrownBy(() -> loginHandler.handle(new LoginCommand("ada", "wrong")))
        .isInstanceOf(InvalidCredentials.class);
  }
}
