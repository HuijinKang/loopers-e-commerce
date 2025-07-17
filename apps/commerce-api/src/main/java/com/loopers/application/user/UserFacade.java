package com.loopers.application.user;

import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;

    public UserInfo createUser(UserV1Dto.UserRequest request) {
        UserModel user = userService.createUser(request);
        return UserInfo.from(user);
    }

    public UserInfo getUser(String userId) {
        UserModel user = userService.getUser(userId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );
        return UserInfo.from(user);
    }
}
