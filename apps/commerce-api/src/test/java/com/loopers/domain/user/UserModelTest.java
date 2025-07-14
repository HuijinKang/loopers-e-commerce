package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserModelTest {

    @DisplayName("UserModel 생성 시")
    @Nested
    class Create {

        @DisplayName("모든 값이 유효하면 정상적으로 생성된다.")
        @Test
        void createsUserModel_whenAllFieldsAreValid() {
            // arrange
            String userId = "chulsoo123";
            String name = "김철수";
            String gender = "M";
            String birth = "2000-01-01";
            String email = "chulsoo@example.com";

            // act
            UserModel user = new UserModel(userId, name, gender, birth, email);

            // assert
            assertAll(
                    () -> assertThat(user.getUserId()).isEqualTo(userId),
                    () -> assertThat(user.getName()).isEqualTo(name),
                    () -> assertThat(user.getGender()).isEqualTo(gender),
                    () -> assertThat(user.getBirth()).isEqualTo(LocalDate.parse(birth)),
                    () -> assertThat(user.getEmail()).isEqualTo(email)
            );
        }

        @DisplayName("userId가 비어있으면 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenUserIdIsBlank() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("   ", "김철수", "M", "2000-01-01", "chulsoo@example.com"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이름이 비어있으면 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenNameIsBlank() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo123", " ", "M", "2000-01-01", "chulsoo@example.com"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("성별이 비어있으면 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenGenderIsBlank() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo123", "김철수", " ", "2000-01-01", "chulsoo@example.com"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 비어있으면 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenBirthIsBlank() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo123", "김철수", "M", " ", "chulsoo@example.com"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일 형식이 잘못되면 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenBirthFormatIsInvalid() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo123", "김철수", "M", "01-01-2000", "chulsoo@example.com"));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 비어있으면 BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequestException_whenEmailIsBlank() {
            CoreException exception = assertThrows(CoreException.class,
                    () -> new UserModel("chulsoo123", "김철수", "M", "2000-01-01", ""));
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}

