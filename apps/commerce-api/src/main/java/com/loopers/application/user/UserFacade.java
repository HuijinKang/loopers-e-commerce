package com.loopers.application.user;

import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.user.UserV1Dto;
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
        UserModel model = userService.getUser(userId);
        return UserInfo.from(model);
    }
}
