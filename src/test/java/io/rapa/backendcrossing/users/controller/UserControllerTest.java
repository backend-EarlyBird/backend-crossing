package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    String userEmail = "wrong@naver.com";
    String userPassword = "wjdt2132131n747";
    String BASE_ENDPOINT = "/api/v1/users";

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        testUser = userRepository.save(
                UserUtils.makeUsers(userEmail, passwordEncoder.encode(userPassword))
        );
    }

    @Nested
    @DisplayName("Context: 올바른 데이터가 주어진 경우")
    class Context_with_available_data{
        @Test
        @DisplayName("It: User 저장 성공하여 201 CREATED와 함께 DTO를 반환")
        void It_User_저장_성공() throws Exception {
            // given
            UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest("notExist@naver.com", userPassword);
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

    @Nested
    @DisplayName("Context: 올바르지 않은 데이터가 주어진 경우")
    class Context_with_invalid_data{


        @Test
        @DisplayName("It: 이미 사용 중인 이메일을 입력 시 409 에러를 발생")
        void It_User_저장_실패__이미_이메일_존재() throws Exception {
            // given
            UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest(userEmail, userPassword);
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
                    andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_ALREAY_EXISTS.getDescription()))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.data").doesNotExist());
        }

        @ParameterizedTest
        @ValueSource(strings = {"1234567", "Abc123!@#Def456$%^Ghi789&*()Jkl012_-+=Mno345[]{}Pqr678XYZ9Stuvw12"})
        @DisplayName("It: 비밀번호를 범위 밖으로 입력 시 400 에러를 발생")
        void It_User_저장_실패__비밀번호_범위_밖(String password) throws Exception {
            // given
            UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest("notExist@naver.com", password);
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
                    andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value(ErrorCode.PASSWORD_LENGTH_NOT_VALID.getDescription()))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.data").doesNotExist());
        }
    }

}