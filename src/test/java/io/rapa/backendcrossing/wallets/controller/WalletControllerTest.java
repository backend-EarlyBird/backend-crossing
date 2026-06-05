package io.rapa.backendcrossing.wallets.controller;

import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepository;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import io.rapa.util.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static io.rapa.backendcrossing.common.constants.ErrorCode.AUTHORIZE_NEEDED;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WalletControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserBoundaryRepository userBoundaryRepository;
    @Autowired
    WalletRepository walletRepository;


    String BASE_URL = "/api/v1/users/me/";

    @Nested
    @DisplayName("Describe : 지갑 조회 GET /api/v1/users/me/wallet")
    class Describe_with_get_wallet{

        Users testUser;
        Wallets testWallet;
        String userEmail = "testuser@naver.com";
        String userPassword = "1234";

        TestingAuthenticationToken authenticationToken;

        @BeforeEach
        void setUp(){
            testUser = userBoundaryRepository.saveUser(
                    UserUtils.makeUsers(userEmail, passwordEncoder.encode(userPassword))
            );
            testWallet = walletRepository.save(
                    Wallets.builder()
                            .user(testUser)
                            .gem(200L)
                            .gold(300L)
                            .build()
            );
            authenticationToken = new TestingAuthenticationToken(
                    CurrentUser.from(
                            testUser
                    )   ,
                    null,
                    "ROLE_" + testUser.getRole()
            );
        }

        @Nested
        @DisplayName("Context : 로그인한 유저ID가 주어진 경우")
        class Context_with_logged_userId{

            @Test
            @DisplayName("It : 유저의 지갑을 조회 후 200 OK를 반환")
            void It_지갑_조회_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URL + "wallet")
                );

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.gold").value(testWallet.getGold()))
                        .andExpect(jsonPath("$.data.gem").value(testWallet.getGem()));
            }
        }

        @Nested
        @DisplayName("Context : 인증되지 않거나, 잘못된 데이터가 주어진 경우")
        class Context_with_unlogged_or_invalid_data{

            @Test
            @DisplayName("It : 인증 되지 않아 지갑 조회에 실패하고, 302 에러가 발생")
            void It_지갑_조회_실패__인증_안됨() throws Exception {
                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_URL + "wallet")
                );

                // then
                actions.andExpect(status().is3xxRedirection());
            }
        }
    }
}