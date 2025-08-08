package com.loopers.application.user;

import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserDomainService;
import com.loopers.interfaces.api.user.UserV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserDomainService userDomainService;

    public UserInfo createUser(UserV1Dto.UserRequest request) {
        userDomainService.validateUserDoesNotExist(request.userId());
        UserModel user = userDomainService.createUser(request);
        return UserInfo.from(user);
    }

    public UserInfo getUser(String userId) {
        UserModel user = userDomainService.getUser(userId);
        return UserInfo.from(user);
    }
}
