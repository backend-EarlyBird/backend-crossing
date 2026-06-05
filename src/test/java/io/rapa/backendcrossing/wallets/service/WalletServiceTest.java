package io.rapa.backendcrossing.wallets.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepository;
import io.rapa.backendcrossing.wallets.domain.dto.WalletDetailResponse;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import io.rapa.util.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("WalletService의")
class WalletServiceTest {

    @Autowired
    WalletService walletService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserBoundaryRepository userBoundaryRepository;
    @Autowired
    WalletRepository walletRepository;

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
    @DisplayName("Describe : getWalletDetail() 메서드는")
    class Describe_with_getWalletDetail{

        @Nested
        @DisplayName("Context : 로그인한 유저의 ID가 올바르게 주어진 경우")
        class Context_with_logged_userId{

            @Test
            @DisplayName("It : 지갑을 조회 후 DTO를 반환한다.")
            void It_지갑_조회_성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // when
                WalletDetailResponse walletDetails = walletService.getWalletDetails(testUser.getUserId());

                // then
                Assertions.assertThat(walletDetails.gem()).isEqualTo(testWallet.getGem());
                Assertions.assertThat(walletDetails.gold()).isEqualTo(testWallet.getGold());
            }
        }

        @Nested
        @DisplayName("Context : 인증되지 않거나 잘못된 데이터가 주어진 경우")
        class Context_with_unlogged_or_invalid_data{

            @Test
            @DisplayName("It : 인증 되지 않아 지갑 조회에 실패한다.")
            void It_지갑_조회_실패__인증_안됨(){

                // when
                Assertions.assertThatThrownBy(
                        ()->walletService.getWalletDetails(testUser.getUserId())
                )
                        // then
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("It : 인증된 사용자와 다른 Email이 인자로 전달되어 지갑 조회에 실패한다.")
            void It_지갑_조회_실패__잘못된_데이터(){
                // given
                SecurityContextHolder.getContext().setAuthentication(
                        new TestingAuthenticationToken(
                                CurrentUser.from(
                                        UserUtils.makeUsers(
                                                "wrong@naver.com",
                                                passwordEncoder.encode("wjdtn213213")
                                        )
                                )   ,
                                null,
                                "ROLE_" + testUser.getRole()
                        )
                );

                // when
                Assertions.assertThatThrownBy(
                                ()->walletService.getWalletDetails(testUser.getUserId())
                        )
                        // then
                        .isInstanceOf(AuthorizationDeniedException.class);
            }
        }
    }
}