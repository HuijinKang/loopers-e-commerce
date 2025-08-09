package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserModelTest {

    @DisplayName("UserModel 생성 시")
    @Nested
    class Create {

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest(name = "잘못된 email: \"{0}\"")
        @ValueSource(strings = {
                "invalid-email",
                "a@b",
                "a@b.",
                "@example.com",
                "user@@example.com"
        })
        @NullAndEmptySource
        void throwsBadRequestException_whenEmailFormatIsInvalidCases(String email) {
            // when & then
            CoreException ex = assertThrows(CoreException.class,
                    () -> new UserModel(email, "강희진", Gender.MALE, "2000-01-01"));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenEmailFormatIsInvalid() {
            // arrange & act & assert
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("invalid-email", "김철수", Gender.MALE, "2000-01-01")); // 잘못된 이메일 형식
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenBirthFormatIsInvalid() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo@example.com", "김철수", Gender.MALE, "01-01-2000"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}

