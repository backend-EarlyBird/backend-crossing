package io.rapa.backendcrossing.auth.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.security.repository.RefreshTokenRepository;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.security.domain.dto.TokenBody;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.auth.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.auth.domain.dto.request.AuthRefreshRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

                TokenBody accessTokenBody = tokenService.parseJwt(accessToken);
                TokenBody refreshTokenBody = tokenService.parseJwt(refreshToken);

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

                TokenBody accessTokenBody = tokenService.parseJwt(accessToken);
                TokenBody refreshTokenBody = tokenService.parseJwt(refreshToken);

                Assertions.assertThat(accessTokenBody.email()).isEqualTo(userEmail);
                Assertions.assertThat(refreshTokenBody.email()).isEqualTo(userEmail);
            }
        }

        @Nested
        @DisplayName("Context: 잘못된 Refresh Token이 주어진 경우")
        class Context_with_invalid_token{


            @Test
            @DisplayName("It: RefreshToken을 재발급 실패한다.")
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

    @Nested
    @DisplayName("Describe: signOut() 메서드는")
    class Describe_with_signOut{

        String refreshToken;

        @BeforeEach
        void setUp(){
            refreshToken = tokenService.issueKeyPair(
                            userEmail,
                            testUser.getRole()
                    )
                    .refreshToken();
        }

        @Nested
        @DisplayName("Context: 올바른 Refresh Token이 주어지는 경우")
        class Context_with_valid_token{

            @Test
            @DisplayName("It: Redis 내 해당 Refresh Token을 모두 삭제 후 로그아웃 성공")
            void It_로그아웃_성공(){
                // given
                Assertions.assertThat(refreshTokenRepository.existsById(refreshToken)).isTrue();

                SecurityContextHolder.getContext().setAuthentication(
                        new TestingAuthenticationToken(
                                CurrentUser.builder()
                                        .email(userEmail)
                                        .nickName(testUser.getNickname()),
                                null,
                                "ROLE_USER"
                        )
                );

                // when
                authService.signOut(refreshToken);

                // then
                Assertions.assertThat(refreshTokenRepository.existsById(refreshToken)).isFalse();
            }
        }

        @Nested
        @DisplayName("Context: 잘못된 인증이나 토큰이 주어진 경우")
        class Context_with_Invalid_token{

            @Test
            @DisplayName("It: 인증 없이 로그아웃 수행 시 401 에러 발생")
            void It_로그아웃_실패_인증_없음(){
                // given
                Assertions.assertThat(refreshTokenRepository.existsById(refreshToken)).isTrue();

                // when
                Assertions.assertThatThrownBy(
                        ()->authService.signOut(refreshToken)
                )
                        // then
                        .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                        .hasMessageContaining("An Authentication object was not found in the SecurityContext");
            }
        }
    }

}