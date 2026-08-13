package com.schoolmanagement.iam.presentation.dto;

import com.schoolmanagement.iam.application.view.UserView;

public record UserResponse(
    String id,
    String username,
    String role,
    String personId) {

  public static UserResponse from(UserView v) {
    return new UserResponse(v.id(), v.username(), v.role(), v.personId());
  }
}
