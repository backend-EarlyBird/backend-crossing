package io.rapa.backendcrossing.profiles.service;

import io.rapa.backendcrossing.profiles.domain.dto.ProfileDetailResponse;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.profiles.repository.ProfileRepository;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepository;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import io.rapa.backendcrossing.wallets.service.WalletService;
import io.rapa.util.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("ProfileService의")
class ProfileServiceTest {
    @Autowired
    ProfileService profileService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserBoundaryRepository userBoundaryRepository;
    @Autowired
    ProfileRepository profileRepository;

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
    class Description_with_valid_user_id{

        @Test
        @DisplayName("It : 프로필을 조회 후 DTO를 반환한다.")
        void It_프로필_조회_성공(){
            // given
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // when
            ProfileDetailResponse foundedDetail = profileService.getDetail(testUser.getUserId());

            // then
            Assertions.assertThat(foundedDetail.exp()).isEqualTo(testProfiles.getExp());
            Assertions.assertThat(foundedDetail.level()).isEqualTo(testProfiles.getLevel());
        }
    }

    @Nested
    @DisplayName("Context : 인증되지 않거나 잘못된 데이터가 주어진 경우")
    class Context_with_unlogged_or_invalid_data{

        @Test
        @DisplayName("It : 인증 되지 않아 프로필 조회에 실패한다.")
        void It_프로필_조회_실패__인증_안됨(){

            // when
            Assertions.assertThatThrownBy(
                            ()->profileService.getDetail(testUser.getUserId())
                    )
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("It : 인증된 사용자와 다른 Email이 인자로 전달되어 프로필 조회에 실패한다.")
        void It_프로필_조회_실패__잘못된_데이터(){
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
                            ()->profileService.getDetail(testUser.getUserId())
                    )
                    // then
                    .isInstanceOf(AuthorizationDeniedException.class);
        }
    }
}