package com.schoolmanagement.iam.application.view;

import com.schoolmanagement.iam.domain.User;

public record UserView(
    String id,
    String username,
    String role,
    String personId) {

    public static UserView from(User u) {
        return new UserView(
                u.id().toString(),
                u.username().value(),
                u.role().name(),
                u.personId() == null ? null : u.personId().toString());
    }
}
