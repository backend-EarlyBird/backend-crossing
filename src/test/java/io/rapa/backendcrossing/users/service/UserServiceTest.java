package io.rapa.backendcrossing.users.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.support.BaseIntegrationTest;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.MeAllDataResponse;
import io.rapa.backendcrossing.users.domain.dto.response.MeDetailResponse;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.util.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserService의")
class UserServiceTest extends BaseIntegrationTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InventoriesRepository inventoriesRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    FriendRequestsRepository friendRepository;

    Users testUser;

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                UserUtils.makeUsers("wrong@email.com", passwordEncoder.encode("wjdtn747"))
        );
        testUser.setLoginTimeNow();
    }

    @Nested
    @DisplayName("Describe: registerUser() 메서드는")
    class Describe_with_register{

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어진 경우")
        class Context_with_available_data{
            @Test
            @DisplayName("It: User 저장 성공")
            void It_User_저장_성공(){
                // given
                UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest("testUser@naver.com", "wjdtn747");
                // when
                UserCreateResponse response = userService.registerUser(userCreateRequest);
                // then
                Assertions.assertThat(response).isNotNull();
                Assertions.assertThat(response.email()).isEqualTo(userCreateRequest.email());

                Users founded = userRepository.findByIdOrThrow(response.userId());
                Assertions.assertThat(founded.getProfile()).isNotNull();
                Assertions.assertThat(founded.getWallet()).isNotNull();
            }
        }

        @Nested
        @DisplayName("Context: 틀린 데이터가 주어진 경우")
        class Context_with_invalid_data{
            @Test
            @DisplayName("It: 이미 존재하는 이메일인 경우 User 저장 실패")
            void It_이메일_중복_시_User_저장_실패(){
                // given
                UserCreateRequest userCreateRequest =  UserUtils.makeCreateRequest(testUser.getEmail(),"wjdtn747");
                // then
                Assertions.assertThatThrownBy(
                        // when
                        () -> userService.registerUser(userCreateRequest)
                )
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ErrorCode.EMAIL_ALREDY_EXISTS.getDescription());
            }
        }
    }

    @Nested
    @DisplayName("Describe: getDetailofMe() 메서드는")
    class Describe_with_getDetailofMe{

        @Nested
        @DisplayName("Context: 유저가 로그인 및 Refresh Token을 발급하여 인증헤더에 포함한 경우")
        class Context_with_logged{
            @Test
            @DisplayName("It: 자신의 정보를 성공적으로 조회가 가능하다.")
            void It_자신의_정보_조회_성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(
                        new TestingAuthenticationToken(
                                CurrentUser.builder()
                                        .email(testUser.getEmail())
                                        .nickName(testUser.getNickname())
                                        .build(),
                                null,
                                "ROLE_" + Role.USER
                        )
                );

                // when
                MeDetailResponse founded = userService.getDetailofMe(testUser.getEmail());

                // then
                Assertions.assertThat(founded).isNotNull();
                Assertions.assertThat(founded.email()).isEqualTo(testUser.getEmail());
                Assertions.assertThat(founded.userId()).isEqualTo(testUser.getUserId());

            }
        }

        @Nested
        @DisplayName("Context: 유저가 로그인하지 않아서 인증헤더에 포함하지 않는 경우")
        class Context_without_logged{
            @Test
            @DisplayName("It: 자신의 정보의 조회가 차단됨")
            void It_자신의_정보_조회_실패__로그인안됨(){
                // when
                Assertions.assertThatThrownBy(
                        ()->userService.getDetailofMe(testUser.getEmail())
                )
                        // then
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    @DisplayName("Describe: getAllDataOfMe 메서드는")
    class Describe_with_getAllDataOfMe{

        Users testUser;
        Wallets testWallet;
        Profiles testProfile;
        Inventories testInventory;
        TestingAuthenticationToken authenticationToken;

        @BeforeEach
        void setUp(){
            UserCreateResponse response = userService.registerUser(UserUtils.makeCreateRequest("testUser@naver.com", "wjdtn747"));
            testUser = userRepository.findByIdOrThrow(response.userId());
            testWallet = testUser.getWallet();
            testProfile = testUser.getProfile();
            testInventory = inventoriesRepository.save(Inventories.builder()
                    .subUserId(testUser.getUserId())
                    .user(testUser).item(savedItem).quantity(3).equipped(false).build());

            friendRepository.save(UserUtils.buildRequest(testUser,
                    userRepository.save(UserUtils.makeUsers("testUser@naver.com1", "wjdtn747"))
                    , FriendRequestsStatus.ACCEPTED));
            friendRepository.save(UserUtils.buildRequest(testUser,
                    userRepository.save(UserUtils.makeUsers("testUser@naver.com2", "wjdtn747"))
                    , FriendRequestsStatus.ACCEPTED));
            friendRepository.save(UserUtils.buildRequest(testUser,
                    userRepository.save(UserUtils.makeUsers("testUser@naver.com3", "wjdtn747"))
                    , FriendRequestsStatus.ACCEPTED));

            authenticationToken = new TestingAuthenticationToken(
                    CurrentUser.from(
                            testUser
                    )   ,
                    null,
                    "ROLE_" + testUser.getRole()
            );
        }

        @Nested
        @DisplayName("Context: 올바른 데이터들이 주어지는 경우")
        class Context_with_valid_data{
            @Test
            @DisplayName("It: 계정의 계정 정보, 프로필, 지갑, 인벤토리, 친구 수를 한 번에 조회 성공 후 반환")
            void It_나의_모든_정보_조회_성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // when
                MeAllDataResponse allDataOfMe = userService.getAllDataOfMe(testUser.getUserId());

                // then
                Assertions.assertThat(allDataOfMe.account().userId()).isEqualTo(testUser.getUserId());
                Assertions.assertThat(allDataOfMe.wallet().gem()).isEqualTo(50000L);
                Assertions.assertThat(allDataOfMe.profile().level()).isEqualTo(0);
                Assertions.assertThat(allDataOfMe.inventory().size()).isGreaterThan(0);
                Assertions.assertThat(allDataOfMe.friendCount()).isEqualTo(3);
            }

            @Test
            @DisplayName("It: 수락 대기중인 친구가 존재 시 미포함해야한다.")
            void It_수락_대기중_미포함(){
                // given
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                friendRepository.save(UserUtils.buildRequest(testUser,
                        userRepository.save(UserUtils.makeUsers("testUser@naver.com4", "wjdtn747"))
                        , FriendRequestsStatus.PENDING));
                friendRepository.save(UserUtils.buildRequest(testUser,
                        userRepository.save(UserUtils.makeUsers("testUser@naver.com5", "wjdtn747"))
                        , FriendRequestsStatus.CANCELED));
                friendRepository.save(UserUtils.buildRequest(testUser,
                        userRepository.save(UserUtils.makeUsers("testUser@naver.com6", "wjdtn747"))
                        , FriendRequestsStatus.DECLINED));

                // when
                MeAllDataResponse allDataOfMe = userService.getAllDataOfMe(testUser.getUserId());

                // then
                Assertions.assertThat(allDataOfMe.friendCount()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("Context: 인증되지 않거나 잘못된 데이터가 주어진 경우")
        class Context_with_unlogged_or_invalid_data{

            @Test
            @DisplayName("It : 인증 되지 않아 모든 정보 조회에 실패한다.")
            void It_모든_정보_조회_실패__인증_안됨(){

                // when
                Assertions.assertThatThrownBy(
                                ()-> userService.getAllDataOfMe(testUser.getUserId())
                        )
                        // then
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("It : 인증된 사용자와 다른 Email이 인자로 전달되어 모든 정보 조회에 실패한다.")
            void It_모든_정보_조회_실패__잘못된_데이터(){
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
                                ()->userService.getAllDataOfMe(testUser.getUserId())
                        )
                        // then
                        .isInstanceOf(AuthorizationDeniedException.class);
            }

        }
    }
}