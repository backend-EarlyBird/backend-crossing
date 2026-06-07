package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.profiles.repository.ProfileRepository;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.support.BaseIntegrationTest;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.MeAllDataResponse;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.users.service.UserService;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import io.rapa.util.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserControllerTest extends BaseIntegrationTest  {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    InventoriesRepository inventoriesRepository;
    @Autowired
    FriendRequestsRepository friendRepository;
    @Autowired
    UserService userService;

    Users testUser;
    String userEmail = "wrong@naver.com";
    String userPassword = "wjdt2132131n747";
    String BASE_ENDPOINT = "/api/v1/users";

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    void setUp(){
        Users mockUser = UserUtils.makeUsers(
                userEmail, passwordEncoder.encode(userPassword)
        );
        testUser = userRepository.save(mockUser);

        walletRepository.save(Wallets.builder().gem(0L).gold(0L).user(testUser).build());
        profileRepository.save(Profiles.builder().level(0).exp(0L).totalPlaySeconds(0L).user(testUser).build());

        testUser = userRepository.findByEmailOrThrow(userEmail);
    }

    @Nested
    @DisplayName("Describe: 회원가입 ( POST /api/v1/users/register )")
    class Describe_with_register{

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
                        .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_ALREDY_EXISTS.getDescription()))
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

    @Nested
    @DisplayName("Describe: 자신의 정보 조회 ( GET /api/v1/users/me )")
    class Describe_with_me_detail{

        String refreshToken;

        @BeforeEach
        void setUp(){
            KeyPair keyPair = tokenService.issueKeyPair(
                    testUser.getEmail(),
                    testUser.getRole()
            );
            refreshToken = keyPair.refreshToken();
        }

        @Nested
        @DisplayName("Context: 유저가 로그인 및 Refresh Token을 발급하여 인증헤더에 포함한 경우")
        class Context_with_logged{
            @Test
            @DisplayName("It: 자신의 정보를 성공적으로 조회 및 200 OK와 함께 응답")
            void It_자신의_정보_조회_성공() throws Exception {

                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(BASE_ENDPOINT + "/me")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken)
                );

                // then
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.userId").value(testUser.getUserId()))
                        .andExpect(jsonPath("$.data.email").value(testUser.getEmail()));
            }
        }

        @Nested
        @DisplayName("Context: 유저가 로그인하지 않아서 인증헤더에 포함하지 않는 경우")
        class Context_without_logged{
            @Test
            @DisplayName("It: 자신의 정보의 조회가 차단 및 302 에러 발생")
            void It_자신의_정보_조회_실패__로그인안됨() throws Exception {
                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(BASE_ENDPOINT + "/me")
                );

                // then
                actions.andExpect(status().is3xxRedirection());

            }
        }
    }

    @Nested
    @DisplayName("Describe: 내 전체 데이터 조회 ( GET /api/v1/users/me/data )")
    class Describe_with_me_all_data{

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
            @DisplayName("It:  조회 성공 후 DTO 반환 및 200 OK 응답")
            void It_나의_모든_정보_조회_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // when
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_ENDPOINT + "/me/data")
                        )
                        // then
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();

                String json = response.getContentAsString();
                MeAllDataResponse foundedData = objectMapper.readValue(
                        json,
                        new TypeReference<CommonResponse<MeAllDataResponse>>() {
                        }
                ).getData();

                // then
                Assertions.assertThat(foundedData.account().userId()).isEqualTo(testUser.getUserId());
                Assertions.assertThat(foundedData.wallet().gem()).isEqualTo(0L);
                Assertions.assertThat(foundedData.profile().level()).isEqualTo(0);
                Assertions.assertThat(foundedData.inventory().size()).isGreaterThan(0);
                Assertions.assertThat(foundedData.friendCount()).isEqualTo(3);
            }

        }

        @Nested
        @DisplayName("Context : 인증되지 않거나, 잘못된 데이터가 주어진 경우")
        class Context_with_unlogged_or_invalid_data{

            @Test
            @DisplayName("It : 인증 되지 않아 모든 정보 조회에 실패하고, 302 에러가 발생")
            void It_나의_모든_정보_조회_실패__인증_안됨() throws Exception {
                // when
                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_ENDPOINT + "/me/data")
                );

                // then
                actions.andExpect(status().is3xxRedirection());
            }
        }
    }

}