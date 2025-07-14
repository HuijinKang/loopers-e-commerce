package com.loopers.application.user;

import com.loopers.domain.user.UserModel;

import java.time.LocalDate;

public record UserInfo (
        String userId,
        String name,
        String gender,
        LocalDate birth,
        String email
) {
    public static UserInfo from(UserModel model) {
        return new UserInfo(
                model.getUserId(),
                model.getName(),
                model.getGender(),
                model.getBirth(),
                model.getEmail()
        );
    }
}
