package io.rapa.backendcrossing.profiles.controller;

import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.profiles.repository.ProfileRepository;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepository;
import io.rapa.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserBoundaryRepository userBoundaryRepository;
    @Autowired
    ProfileRepository profileRepository;


    String BASE_URL = "/api/v1/users/me";

    @Nested
    @DisplayName("Describe : 프로필 조회 GET /api/v1/users/me/profile")
    class Describe_with_get_profile{

        Users testUser;
        Profiles testProfiles;
        String userEmail = "testuser@naver.com";
        String userPassword = "1234";

        TestingAuthenticationToken authenticationToken;

        @BeforeEach
        void setUp(){
            testUser = userBoundaryRepository.saveUser(
                    UserUtils.makeUsers(userEmail, passwordEncoder.encode(userPassword))
            );
            testProfiles = profileRepository.save(
                    testProfiles.builder()
                            .level(0)
                            .exp(0L)
                            .totalPlaySeconds(0L)
                            .user(testUser)
                            .build()
            ) ;
            authenticationToken = new TestingAuthenticationToken(
                    CurrentUser.from(
                            testUser
                    )   ,
                    null,
                    "ROLE_" + testUser.getRole()
            );
        }

        @Nested
        @DisplayName("Context : 로그인한 유저의 ID가 올바르게 주어진 경우")
        class Context_with_logged_userId{

            @Test
            @DisplayName("It : 유저의 프로필을 조회 후 200 OK를 반환")
            void It_프로필_조회_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URL + "/profile")
                );

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.level").value(testProfiles.getLevel()))
                        .andExpect(jsonPath("$.data.exp").value(testProfiles.getExp()))
                        .andExpect(jsonPath("$.data.totalPlaySeconds").value(testProfiles.getTotalPlaySeconds()));
            }
        }

        @Nested
        @DisplayName("Context : 인증되지 않거나, 잘못된 데이터가 주어진 경우")
        class Context_with_unlogged_or_invalid_data{

            @Test
            @DisplayName("It : 인증 되지 않아 프로필 조회에 실패하고, 302 에러가 발생")
            void It_프로필_조회_실패__인증_안됨() throws Exception {
                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URL + "profile")
                );

                // then
                actions.andExpect(status().is3xxRedirection());
            }
        }
    }
}