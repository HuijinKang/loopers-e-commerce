package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
        Optional<UserModel> existingUser = getUser(request.userId());

        if (existingUser.isPresent()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 가입된 사용자입니다.");
        }

        UserModel user = UserModel.from(request);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserModel> getUser(String userId) {
        return userRepository.findByUserId(userId);
    }
}
