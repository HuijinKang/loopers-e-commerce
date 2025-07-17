package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserModelTest {

    @DisplayName("UserModel 생성 시")
    @Nested
    class Create {

        @DisplayName("ID가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenUserIdFormatIsInvalid() {
            // 1. 11자 이상 (길이 초과)
            CoreException ex1 = assertThrows(CoreException.class,
                    () -> new UserModel("abcdefghijk", "김철수", "M", "2000-01-01", "chulsoo@example.com"));
            assertThat(ex1.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);

            // 2. 한글/특수문자 포함
            CoreException ex2 = assertThrows(CoreException.class,
                    () -> new UserModel("철수123!!", "김철수", "M", "2000-01-01", "chulsoo@example.com"));
            assertThat(ex2.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);

            // 3. 공백 포함
            CoreException ex3 = assertThrows(CoreException.class,
                    () -> new UserModel("chul soo", "김철수", "M", "2000-01-01", "chulsoo@example.com"));
            assertThat(ex3.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);

            // 4. null
            CoreException ex4 = assertThrows(CoreException.class,
                    () -> new UserModel(null, "김철수", "M", "2000-01-01", "chulsoo@example.com"));
            assertThat(ex4.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
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

