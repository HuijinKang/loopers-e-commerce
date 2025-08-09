package com.loopers.application.user;

import com.loopers.domain.user.UserDomainService;
import com.loopers.domain.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserQueryFacade {

    private final UserDomainService userDomainService;

    @Transactional(readOnly = true)
    public UserInfo getUser(String email) {
        UserModel user = userDomainService.getUser(email);
        return UserInfo.from(user);
    }
}
