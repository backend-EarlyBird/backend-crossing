package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.users.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Describe: 회원가입( POST /api/v1/users/register )")
class AuthControllerTest {

    @Autowired
    AuthController authController;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
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

    @Nested
    @DisplayName("Context: 올바른 이메일 / 비밀번호가 주어진 경우")
    class Context_with_available_data{
        @Test
        @DisplayName("It: 로그인을 성공하여 200 OK와 함께 JWT Token을 반환")
        void It_Login_success_with_200_OK() throws Exception {
            // given
            AuthLoginRequest request = UserUtils.makeLoginRequest(userEmail, userPassword);
            // when
            String json = objectMapper.writeValueAsString(request);
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