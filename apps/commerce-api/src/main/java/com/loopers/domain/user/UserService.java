package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserModel createUser(UserV1Dto.UserRequest request) {

        UserModel user = UserModel.from(request);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserModel> getUser(String userId) {
        return userRepository.findByUserId(userId);
    }
}
