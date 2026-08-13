package com.schoolmanagement.iam.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;

@Repository
public class UserRepositoryAdapter implements UserRepository {
  private final UserJpaRepository userRepository;
  private final UserMapper userMapper;

  public UserRepositoryAdapter(UserJpaRepository jpa, UserMapper mapper) {
    this.userRepository = jpa;
    this.userMapper = mapper;
  }

  @Override
  public void save(User user) {
    userRepository.save(userMapper.toEntity(user));
  }

  @Override
  public Optional<User> findByUsername(Username username) {
    return userRepository.findByUsername(username.value())
        .map(userMapper::toDomain);
  }
}
