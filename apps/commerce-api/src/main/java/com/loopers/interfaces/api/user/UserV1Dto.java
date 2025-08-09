package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.Gender;

import java.time.LocalDate;

public class UserV1Dto {

    public record UserRequest (
            String email,
            String name,
            Gender gender,
            String birth
    ) { }

    public record UserResponse (
            String email,
            String name,
            Gender gender,
            LocalDate birth
    ) {
        public static UserV1Dto.UserResponse from(UserInfo info) {
            return new UserV1Dto.UserResponse(
                    info.email(),
                    info.name(),
                    info.gender(),
                    info.birth()
            );
        }
    }
}
