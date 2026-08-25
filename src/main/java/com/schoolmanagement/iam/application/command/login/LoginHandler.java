package com.schoolmanagement.iam.application.command.login;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.iam.application.view.LoginView;
import com.schoolmanagement.iam.domain.PasswordHasher;
import com.schoolmanagement.iam.domain.TokenIssuer;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;
import com.schoolmanagement.iam.domain.exception.InvalidCredentials;
  
@Service
public class LoginHandler {
  private final UserRepository users;
  private final PasswordHasher passwordHasher;
  private final TokenIssuer tokenIssuer;

  public LoginHandler(UserRepository users, PasswordHasher passwordHasher, TokenIssuer tokenIssuer) {
    this.users = users;
    this.passwordHasher = passwordHasher;
    this.tokenIssuer = tokenIssuer;
  }

  @Transactional(readOnly = true)
  public LoginView handle(LoginCommand command) {
    User user = users.findByUsername(new Username(command.username()))
        .orElseThrow(InvalidCredentials::new);

    if (!user.enabled() || !passwordHasher.matches(command.password(), user.passwordHash())) {
      throw new InvalidCredentials();
    }

    return new LoginView(tokenIssuer.issue(user));
  }
}
