package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(UserV1Dto.UserRequest request) {
        UserModel user = UserModel.from(request);
        userRepository.save(user);
    }

    public Optional<UserModel> getUser(String userId) {
        return userRepository.findByUserId(userId);
    }
}
