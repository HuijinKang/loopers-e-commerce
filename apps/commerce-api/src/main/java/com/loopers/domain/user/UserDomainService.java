package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;

    @Transactional
    public UserModel createUser(UserV1Dto.UserRequest request) {
        UserModel user = UserModel.from(request);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserModel getUser(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다)")
        );
    }

    @Transactional(readOnly = true)
    public void validateUserDoesNotExist(String userId) {
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 사용자입니다.");
        }
    }
}
