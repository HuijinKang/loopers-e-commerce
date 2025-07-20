package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.time.LocalDate;

public class UserValidator {

    public static String validateUserId(String userId) {
        if (userId == null || userId.isBlank())
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 비어있을 수 없습니다.");
        if (!userId.matches("^[a-zA-Z0-9]{1,10}$"))
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내여야 합니다.");
        return userId;
    }

    public static String validateName(String name) {
        if (name == null || name.isBlank())
            throw new CoreException(ErrorType.BAD_REQUEST, "이름은 비어있을 수 없습니다.");
        return name;
    }

    public static String validateGender(String gender) {
        if (gender == null || gender.isBlank())
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 비어있을 수 없습니다.");
        return gender;
    }

    public static String validateEmail(String email) {
        if (email == null || email.isBlank())
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 비어있을 수 없습니다.");
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        return email;
    }

    public static LocalDate validateBirth(String birth) {
        if (birth == null || birth.isBlank())
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 비어있을 수 없습니다.");
        try {
            return LocalDate.parse(birth);
        } catch (Exception e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다. (예: 1990-01-01)");
        }
    }
}
