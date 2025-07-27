package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.Gender;

import java.time.LocalDate;

public class UserV1Dto {

    public record UserRequest (
            String userId,
            String name,
            Gender gender,
            String birth,
            String email
    ) {
    }

    public record UserResponse (
            String userId,
            String name,
            Gender gender,
            LocalDate birth,
            String email
    ) {
        public static UserV1Dto.UserResponse from(UserInfo info) {
            return new UserV1Dto.UserResponse(
                    info.userId(),
                    info.name(),
                    info.gender(),
                    info.birth(),
                    info.email()
            );
        }
    }
}
