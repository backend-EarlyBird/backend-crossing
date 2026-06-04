package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.infra.domain.entity.RefreshToken;
import io.rapa.backendcrossing.infra.repository.RefreshTokenRepository;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.users.domain.dto.request.AuthRefreshRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TokenService tokenService;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    MockMvc mockMvc;

    Users testUser;
    String userEmail = "testuser@naver.com";
    String userPassword = "1234";
    String BASE_ENDPOINT = "/api/v1/auth";

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                UserUtils.makeUsers(userEmail, passwordEncoder.encode(userPassword))
        );
    }

    @DisplayName("Describe: 로그인( POST /api/v1/auth/login )")
    class Describe_with_login{

        @Nested
        @DisplayName("Context: 올바른 이메일 / 비밀번호가 주어진 경우")
        class Context_with_available_data{
            @Test
            @DisplayName("It: 로그인을 성공하여 200 OK와 함께 JWT Token을 반환")
            void It_Login_success_with_200_OK() throws Exception {
                // given
                AuthLoginRequest request = UserUtils.makeLoginRequest(userEmail, userPassword);
                String json = objectMapper.writeValueAsString(request);
                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(BASE_ENDPOINT + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                );
                // then
                actions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value(SuccessMessage.LOGIN_SUCCESS.getMessage()))
                        .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                        .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Describe: RefreshToken 재발급 ( POST /api/v1/auth/refresh )")
    class Describe_with_refreshToken{

        String refreshToken;

        @BeforeEach
        void setUp(){
            refreshToken = tokenService.issueKeyPair(
                    testUser.getEmail(),
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
        class Context_with_available_data{
            @Test
            @DisplayName("It: Refresh Token을 전달 후 200 OK와 함께 JWT Token을 반환")
            void It_Refresh_success_with_200_OK() throws Exception{
                // given
                AuthRefreshRequest refreshRequest = new AuthRefreshRequest(refreshToken);

                String json = objectMapper.writeValueAsString(refreshRequest);

                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(BASE_ENDPOINT + "/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                );

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                        .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());

            }
        }

        @Nested
        @DisplayName("Context: 유효하지 않은 Refresh Token이 주어진 경우")
        class Context_with_invalid_data{
            @Test
            @DisplayName("It: Refresh Token을 전달 후 401 에러 발생")
            void It_Refresh_success_with_200_OK() throws Exception{
                // given
                AuthRefreshRequest refreshRequest = new AuthRefreshRequest("invalid Token");

                String json = objectMapper.writeValueAsString(refreshRequest);

                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(BASE_ENDPOINT + "/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                );

                // then
                actions.andExpect(status().is4xxClientError())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.data").isEmpty())
                        .andExpect(jsonPath("$.message").value(ErrorCode.ABNORMAL_REFRESH_TOKEN.getDescription()));

            }
        }

    }



}