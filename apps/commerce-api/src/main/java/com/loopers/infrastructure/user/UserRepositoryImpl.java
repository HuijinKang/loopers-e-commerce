package com.loopers.infrastructure.user;

import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<UserModel> findByUserId(String userId) {
        return userJpaRepository.findByUserId(userId);
    }

    @Override
    public UserModel save(UserModel user) {
        userJpaRepository.save(user);
        return user;
    }
}
