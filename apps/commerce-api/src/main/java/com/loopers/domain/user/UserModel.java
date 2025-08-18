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

    private String email;
    private String name;
    private Gender gender;
    private LocalDate birth;

    public UserModel(String email, String name, Gender gender, String birth) {
        this.email = UserValidator.validateEmail(email);
        this.name = UserValidator.validateName(name);
        this.gender = UserValidator.validateGender(gender);
        this.birth = UserValidator.validateBirth(birth);
    }

    public static UserModel of(String email, String name, Gender gender, String birth) {
        return new UserModel(email, name, gender, birth);
    }

    public static UserModel from(UserV1Dto.UserRequest request) {
        return new UserModel(
                request.email(),
                request.name(),
                request.gender(),
                request.birth()
        );
    }
}
