package io.rapa.backendcrossing.users.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.MeDetailResponse;
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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;

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

    Users testUser;

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                UserUtils.makeUsers("wrong@email.com", passwordEncoder.encode("wjdtn747"))
        );
        testUser.setLoginTimeNow();
    }

    @Nested
    @DisplayName("Describe: registerUser() 메서드는")
    class Describe_with_register{

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어진 경우")
        class Context_with_available_data{
            @Test
            @DisplayName("It: User 저장 성공")
            void It_User_저장_성공(){
                // given
                UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest("testUser@naver.com", "wjdtn747");
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
                UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest(testUser.getEmail(),"wjdtn747");
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

    @Nested
    @DisplayName("Describe: getDetailofMe() 메서드는")
    class Describe_with_getDetailofMe{

        @Nested
        @DisplayName("Context: 유저가 로그인 및 Refresh Token을 발급하여 인증헤더에 포함한 경우")
        class Context_with_logged{
            @Test
            @DisplayName("It: 자신의 정보를 성공적으로 조회가 가능하다.")
            void It_자신의_정보_조회_성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(
                        new TestingAuthenticationToken(
                                CurrentUser.builder()
                                        .email(testUser.getEmail())
                                        .nickName(testUser.getNickName())
                                        .build(),
                                null,
                                "ROLE_" + Role.USER
                        )
                );

                // when
                MeDetailResponse founded = userService.getDetailofMe(testUser.getEmail());

                // then
                Assertions.assertThat(founded).isNotNull();
                Assertions.assertThat(founded.email()).isEqualTo(testUser.getEmail());
                Assertions.assertThat(founded.userId()).isEqualTo(testUser.getUserId());

            }
        }

        @Nested
        @DisplayName("Context: 유저가 로그인하지 않아서 인증헤더에 포함하지 않는 경우")
        class Context_without_logged{
            @Test
            @DisplayName("It: 자신의 정보의 조회가 차단됨")
            void It_자신의_정보_조회_실패__로그인안됨(){
                // when
                Assertions.assertThatThrownBy(
                        ()->userService.getDetailofMe(testUser.getEmail())
                )
                        // then
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}