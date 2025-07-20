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

        @DisplayName("ID가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest(name = "잘못된 userId: \"{0}\"")
        @ValueSource(strings = {
                "abcdefghijk",   // 11자 이상 (길이 초과) X
                "희진123!!",      // 한글/특수문자 X
                "hj 123"         // 공백 X
        })
        @NullAndEmptySource // null X
        void throwsBadRequestException_whenUserIdFormatIsInvalid(String userId) {
            // when & then
            CoreException ex = assertThrows(CoreException.class,
                    () -> new UserModel(userId, "강희진", "F", "2000-01-01", "hj@example.com"));
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenEmailFormatIsInvalid() {
            // arrange & act & assert
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo123", "김철수", "M", "2000-01-01", "invalid-email")); // 잘못된 이메일 형식
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenBirthFormatIsInvalid() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo123", "김철수", "M", "01-01-2000", "chulsoo@example.com"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}

