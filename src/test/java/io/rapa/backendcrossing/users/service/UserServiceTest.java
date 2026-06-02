package io.rapa.backendcrossing.users.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("UserService의")
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("Describe: registerUser() 메서드는")
    class Describe_with_register{

        Users testUser;

        @BeforeEach
        void setUp(){
            testUser = userRepository.save(
                    UserUtils.makeUsers("wrong@email.com", passwordEncoder.encode("1234"))
            );
        }

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어진 경우")
        class Context_with_available_data{
            @Test
            @DisplayName("It: User 저장 성공")
            void It_User_저장_성공(){
                // given
                UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest("testUser@naver.com");
                // when
                UserCreateResponse response = userService.registerUser(userCreateRequest);
                // then
                Assertions.assertThat(response).isNotNull();
                Assertions.assertThat(response.email()).isEqualTo(userCreateRequest.email());
            }
        }

        @Nested
        @DisplayName("Context: 틀린 데이터가 주어진 경우")
        class Context_with_invalid_data{
            @Test
            @DisplayName("It: 이미 존재하는 이메일인 경우 User 저장 실패")
            void It_이메일_중복_시_User_저장_실패(){
                // given
                UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest(testUser.getEmail());
                // then
                Assertions.assertThatThrownBy(
                        // when
                        () -> userService.registerUser(userCreateRequest)
                )
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ErrorCode.EMAIL_ALREAY_EXISTS.getDescription());
            }
        }
    }
}