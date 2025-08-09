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
        userDomainService.validateUserDoesNotExist(request.email());
        UserModel user = userDomainService.createUser(request);
        return UserInfo.from(user);
    }

    public UserInfo getUser(String email) {
        UserModel user = userDomainService.getUser(email);
        return UserInfo.from(user);
    }
}
