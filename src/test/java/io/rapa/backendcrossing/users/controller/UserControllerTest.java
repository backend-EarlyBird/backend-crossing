package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Describe: 회원가입 ( POST /api/v1/users/register )")
class UserControllerTest {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    Users testUser;
    String userEmail = "testUser@naver.com";
    String userPassword = "1234";
    String BASE_ENDPOINT = "/api/v1/users";

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                UserUtils.makeUsers("wrong@naver.com", passwordEncoder.encode(userPassword))
        );
    }

    @Nested
    @DisplayName("Context: 올바른 데이터가 주어진 경우")
    class Context_with_available_data{
        @Test
        @DisplayName("It: User 저장 성공하여 201 CREATED와 함께 DTO를 반환")
        void It_User_저장_성공() throws Exception {
            // given
            UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest(userEmail);
            String json = objectMapper.writeValueAsString(userCreateRequest);
            // when
            ResultActions actions = mockMvc.perform(
                    MockMvcRequestBuilders
                            .post(BASE_ENDPOINT + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
            );
            // then
            actions.
                    andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value(SuccessMessage.USER_CREATE_SUCCESS.getMessage()))
                    .andExpect(jsonPath("$.data.nickname").value(userCreateRequest.nickname()))
                    .andExpect(jsonPath("$.data.email").value(userCreateRequest.email()));
        }
    }

}