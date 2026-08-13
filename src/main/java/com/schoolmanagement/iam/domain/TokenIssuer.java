package com.schoolmanagement.iam.domain;

public interface TokenIssuer {
    String issue(User user);
}
