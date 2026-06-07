package io.rapa.backendcrossing.friendRequests.repository;

import io.rapa.backendcrossing.common.config.JpaConfig;
import io.rapa.backendcrossing.common.config.PasswordConfiguration;
import io.rapa.backendcrossing.common.config.QueryDslConfiguration;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.dto.response.MeAllDataResponse;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.domain.entity.BaseEntity;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.users.service.UserService;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.util.UserUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


@DataJpaTest
@Import({
        QueryDslConfiguration.class, PasswordConfiguration.class,JpaConfig.class
})
@ActiveProfiles("test")
@DisplayName("Describe : FriendRequestRepository의")
class FriendRepositoryTest {

    @Autowired
    FriendRequestsRepository repository;

    @Nested
    @DisplayName("Describe : countFriendByUserId()은")
    class Describe_with_countFriendByUserId{

        @Autowired
        UserRepository userRepository;
        @Autowired
        PasswordEncoder passwordEncoder;
        @Autowired
        FriendRequestsRepository friendRepository;

        Users testUser;
        String userEmail = "wrong@naver.com";
        String userPassword = "wjdt2132131n747";

        @BeforeEach
        void setUp(){
            testUser = userRepository.save(
                    Users.builder().email(userEmail).nickname("정수리").password(userPassword).build()
            );

            friendRepository.save(UserUtils.buildRequest(testUser,
                    userRepository.save(UserUtils.makeUsers("testUser1@naver.com1", "wjdtn747"))
                    , FriendRequestsStatus.ACCEPTED));
            friendRepository.save(UserUtils.buildRequest(testUser,
                    userRepository.save(UserUtils.makeUsers("testUser2@naver.com2", "wjdtn747"))
                    , FriendRequestsStatus.ACCEPTED));
            friendRepository.save(UserUtils.buildRequest(testUser,
                    userRepository.save(UserUtils.makeUsers("testUser3@naver.com3", "wjdtn747"))
                    , FriendRequestsStatus.ACCEPTED));
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_valid_data{
            @Test
            @DisplayName("It : 성공적으로 해당 유저의 친구수를 측정 후 반환")
            void It_친구수_세기_성공(){
                // when
                Integer count = friendRepository.countFriendByUserId(testUser.getUserId());

                // then
                Assertions.assertThat(count).isEqualTo(3);
            }

            @Test
            @DisplayName("It: 수락 대기중인 친구가 존재 시 미포함한 친구수를 반환")
            void It_수락_대기중_미포함(){
                // given
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
                Integer count = friendRepository.countFriendByUserId(testUser.getUserId());

                // then
                Assertions.assertThat(count).isEqualTo(3);
            }
        }
    }

}