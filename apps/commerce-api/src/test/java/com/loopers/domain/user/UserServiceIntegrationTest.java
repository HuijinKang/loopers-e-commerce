package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @InjectMocks
    private UserService userService2;

    @Spy
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("사용자를 생성할 때,")
    @Nested
    class Create {

        @Test
        @DisplayName("회원 가입시 User 저장이 수행된다. ( spy 검증 )")
        void savesUser_whenUserCreated_withSpy() {
            // arrange
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "chulsoo123",
                    "김철수",
                    "M",
                    "2000-01-01",
                    "chulsoo@example.com"
            );

            // act
            userService2.createUser(request);

            // assert
            verify(userRepository).save(any(UserModel.class));
        }

        @DisplayName("이미 가입된 ID로 회원 저장을 시도하면 실패한다.")
        @Test
        void throwsException_whenUserIdAlreadyExists_directJpaSave() {
            // arrange
            UserV1Dto.UserRequest request1 = new UserV1Dto.UserRequest(
                    "chulsoo123",
                    "김철수",
                    "M",
                    "2000-01-01",
                    "chulsoo@example.com"
            );
            userService.createUser(request1);

            UserV1Dto.UserRequest request2 = new UserV1Dto.UserRequest(
                    "chulsoo123",
                    "홍길동",
                    "M",
                    "1995-05-05",
                    "hong@example.com"
            );

            // act & assert
            CoreException exception = assertThrows(
                    CoreException.class,
                    () -> userService.createUser(request2)
            );

            assertThat(exception.getErrorType().getStatus()).isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(exception.getMessage()).isEqualTo("이미 가입된 사용자입니다.");
        }
    }

    @DisplayName("사용자를 조회할 때,")
    @Nested
    class Get {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void returnsUser_whenValidUserIdProvided() {
            // arrange
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "huijin123",
                    "희진",
                    "M",
                    "1999-01-01",
                    "huijin123@example.com"
            );
            userService.createUser(request);
            // act
            Optional<UserModel> result = userService.getUser("huijin123");

            // assert
            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isEqualTo("huijin123");
            assertThat(result.get().getName()).isEqualTo("희진");
            assertThat(result.get().getEmail()).isEqualTo("huijin123@example.com");
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void throwsNotFoundException_whenUserIdDoesNotExist() {
            // arrange
            String invalidId = "none";

            // act
            Optional<UserModel> user = userService.getUser(invalidId);

            // assert
            assertThat(user).isEqualTo(Optional.empty());
        }
    }
}
