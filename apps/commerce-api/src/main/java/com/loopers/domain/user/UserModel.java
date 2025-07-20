package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.interfaces.api.user.UserV1Dto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserModel extends BaseEntity {

    @Column(name = "user_id", unique = true)
    private String userId;
    private String name;
    private String gender;
    private LocalDate birth;
    private String email;

    public UserModel(String userId, String name, String gender, String birth, String email) {
        this.userId = UserValidator.validateUserId(userId);
        this.name = UserValidator.validateName(name);
        this.gender = UserValidator.validateGender(gender);
        this.birth = UserValidator.validateBirth(birth);
        this.email = UserValidator.validateEmail(email);
    }

    public static UserModel from(UserV1Dto.UserRequest request) {
        return new UserModel(
                request.userId(),
                request.name(),
                request.gender(),
                request.birth(),
                request.email()
        );
    }
}
