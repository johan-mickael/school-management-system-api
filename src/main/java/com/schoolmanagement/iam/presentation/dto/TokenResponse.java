package com.schoolmanagement.iam.presentation.dto;

import com.schoolmanagement.iam.application.view.LoginView;

public record TokenResponse(String token) {

  public static TokenResponse from(LoginView v) {
    return new TokenResponse(v.token());
  }
}
