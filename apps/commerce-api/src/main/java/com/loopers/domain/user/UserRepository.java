package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<UserModel> findByUserId(String userId);
    void save(UserModel user);
}
