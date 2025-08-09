package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<UserModel> findById(Long id);
    Optional<UserModel> findByEmail(String email);
    UserModel save(UserModel user);
}
