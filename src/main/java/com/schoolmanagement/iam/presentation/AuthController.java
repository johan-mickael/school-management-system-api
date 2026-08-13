package com.schoolmanagement.iam.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.iam.application.command.login.LoginCommand;
import com.schoolmanagement.iam.application.command.login.LoginHandler;
import com.schoolmanagement.iam.application.command.registeruser.RegisterUserCommand;
import com.schoolmanagement.iam.application.command.registeruser.RegisterUserHandler;
import com.schoolmanagement.iam.application.view.UserView;
import com.schoolmanagement.iam.presentation.dto.LoginRequest;
import com.schoolmanagement.iam.presentation.dto.RegisterUserRequest;
import com.schoolmanagement.iam.presentation.dto.TokenResponse;
import com.schoolmanagement.iam.presentation.dto.UserResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {

  private final LoginHandler login;
  private final RegisterUserHandler register;

  public AuthController(LoginHandler login, RegisterUserHandler register) {
    this.login = login;
    this.register = register;
  }

  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest request) {
    return TokenResponse.from(login.handle(new LoginCommand(request.username(), request.password())));
  }

  @PostMapping("/register")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
    RegisterUserCommand registerUserCommand = new RegisterUserCommand(
        request.username(),
        request.password(),
        request.role(),
        request.personId());
    UserView view = register.handle(registerUserCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/users/" + view.id()))
        .body(UserResponse.from(view));
  }
}
