package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
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

        @DisplayName("올바른 요청이 주어지면 정상적으로 저장된다.")
        @Test
        void savesUser_whenValidRequestProvided() {
            // arrange
            UserV1Dto.UserRequest request = new UserV1Dto.UserRequest(
                    "chulsoo123",
                    "김철수",
                    "M",
                    "2000-01-01",
                    "chulsoo@example.com"
            );

            // act
            userService.createUser(request);

            // assert
            UserModel saved = userRepository.findByUserId("chulsoo123").orElseThrow();
            assertAll(
                    () -> assertThat(saved.getUserId()).isEqualTo("chulsoo123"),
                    () -> assertThat(saved.getName()).isEqualTo("김철수"),
                    () -> assertThat(saved.getGender()).isEqualTo("M"),
                    () -> assertThat(saved.getBirth()).isEqualTo("2000-01-01"),
                    () -> assertThat(saved.getEmail()).isEqualTo("chulsoo@example.com")
            );
        }
    }

    @DisplayName("사용자를 조회할 때,")
    @Nested
    class Get {

        @DisplayName("존재하는 사용자 ID를 주면, 해당 사용자 정보를 반환한다.")
        @Test
        void returnsUser_whenValidUserIdProvided() {
            // arrange
            UserModel user = new UserModel("younghee123", "김영희", "F", "1999-01-01", "younghee@example.com");
            userRepository.save(user);

            // act
            Optional<UserModel> result = userService.getUser("younghee123");

            // assert
            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isEqualTo("younghee123");
            assertThat(result.get().getName()).isEqualTo("김영희");
            assertThat(result.get().getEmail()).isEqualTo("younghee@example.com");
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
