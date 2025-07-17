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

    public void createUser(UserV1Dto.UserRequest request) {
        userService.createUser(request);
    }

    public UserInfo getUser(String userId) {
        UserModel model = userService.getUser(userId).orElseThrow(
                () -> new CoreException(ErrorType.BAD_REQUEST, "유저를 찾을 수 없습니다.")
        );
        return UserInfo.from(model);
    }
}
