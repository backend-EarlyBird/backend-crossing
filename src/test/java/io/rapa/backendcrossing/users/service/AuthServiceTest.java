package io.rapa.backendcrossing.users.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.infra.repository.RefreshTokenRepository;
import io.rapa.backendcrossing.security.constants.TokenType;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.security.domain.dto.TokenBody;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.users.domain.dto.request.AuthRefreshRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@DisplayName("AuthService의")
class AuthServiceTest {
    @Autowired
    AuthService authService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    TokenService tokenService;



    Users testUser;
    String userEmail = "testuser@naver.com";
    String userPassword = "12345678";

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                UserUtils.makeUsers(userEmail, passwordEncoder.encode(userPassword))
        );
    }

    @Nested
    @DisplayName("Describe: signIn() 메서드는")
    class Describe_with_SignIn{

        @Nested
        @DisplayName("Context: 올바른 이메일과 비밀번호가 주어진 경우")
        class Context_with_availabe_data{
            @Test
            @DisplayName("It: 로그인을 성공하여 JWT 토큰을 반환")
            void It_로그인_성공(){
                // given
                AuthLoginRequest authLoginRequest = UserUtils.makeLoginRequest(userEmail,userPassword);
                // when
                KeyPair keyPair = authService.signIn(authLoginRequest);
                // then
                String accessToken = keyPair.accessToken();
                String refreshToken = keyPair.refreshToken();
                Assertions.assertThat(accessToken).isNotNull();
                Assertions.assertThat(refreshToken).isNotNull();

                TokenBody accessTokenBody = tokenService.parseJwt(accessToken, TokenType.ACCESS_TOKEN);
                TokenBody refreshTokenBody = tokenService.parseJwt(refreshToken, TokenType.REFRESH_TOKEN);

                Assertions.assertThat(accessTokenBody.email()).isEqualTo(authLoginRequest.email());
                Assertions.assertThat(refreshTokenBody.email()).isEqualTo(authLoginRequest.email());
            }
        }
    }


    @Nested
    @DisplayName("Describe: refreshToken() 메서드는")
    class Describe_with_Refresh{

        String refreshToken;

        @BeforeEach
        void setUp(){
            refreshToken = tokenService.issueKeyPair(
                            userEmail,
                            testUser.getRole()
                    )
                    .refreshToken();
        }

        @AfterEach
        void tearDrop(){
            refreshTokenRepository.deleteAll();
        }


        @Nested
        @DisplayName("Context: 올바른 Refresh Token이 주어진 경우")
        class Context_with_valid_token{


            @Test
            @DisplayName("It: RefreshToken을 재발급 성공한다.")
            void It_RefreshToken_reissue_success(){
                // given
                AuthRefreshRequest request = new AuthRefreshRequest(refreshToken);

                // when
                KeyPair keyPair = authService.refreshToken(request);

                // then
                String accessToken = keyPair.accessToken();
                String refreshToken = keyPair.refreshToken();
                Assertions.assertThat(accessToken).isNotNull();
                Assertions.assertThat(refreshToken).isNotNull();

                TokenBody accessTokenBody = tokenService.parseJwt(accessToken, TokenType.ACCESS_TOKEN);
                TokenBody refreshTokenBody = tokenService.parseJwt(refreshToken, TokenType.REFRESH_TOKEN);

                Assertions.assertThat(accessTokenBody.email()).isEqualTo(userEmail);
                Assertions.assertThat(refreshTokenBody.email()).isEqualTo(userEmail);
            }
        }

        @Nested
        @DisplayName("Context: 잘못된 Refresh Token이 주어진 경우")
        class Context_with_invalid_token{


            @Test
            @DisplayName("It: RefreshToken을 재발급 성공한다.")
            void It_RefreshToken_reissue_success(){
                // given
                AuthRefreshRequest request = new AuthRefreshRequest("invalidToken");

                // when
                Assertions.assertThatThrownBy(
                        ()-> authService.refreshToken(request)
                )
                        // then
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ErrorCode.ABNORMAL_REFRESH_TOKEN.getDescription());
            }
        }
    }
}