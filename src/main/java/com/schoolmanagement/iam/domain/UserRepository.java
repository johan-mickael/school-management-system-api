package com.schoolmanagement.iam.domain;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findByUsername(Username username);

    Optional<User> findById(UserId id);
}
