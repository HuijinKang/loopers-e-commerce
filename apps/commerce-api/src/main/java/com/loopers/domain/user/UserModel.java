package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.interfaces.api.user.UserV1Dto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserModel extends BaseEntity {

    private String userId;
    private String name;
    private String gender;
    private LocalDate birth;
    private String email;

    public UserModel(String userId, String name, String gender, String birth, String email) {
        if (userId == null || userId.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 비어있을 수 없습니다.");
        }
        if (name == null || name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이름은 비어있을 수 없습니다.");
        }
        if (gender == null || gender.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 비어있을 수 없습니다.");
        }
        if (birth == null || birth.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어있을 수 없습니다.");
        }
        if (email == null || email.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 비어있을 수 없습니다.");
        }

        LocalDate parsedBirth;
        try {
            parsedBirth = LocalDate.parse(birth);
        } catch (Exception e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다. (예: 1990-01-01)");
        }

        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.birth = parsedBirth;
        this.email = email;
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
