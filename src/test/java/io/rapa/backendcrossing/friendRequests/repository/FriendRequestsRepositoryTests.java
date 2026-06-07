package io.rapa.backendcrossing.friendRequests.repository;

import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.rapa.backendcrossing.common.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.autoconfigure.DataSourceInitializationAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.controller
 * fileName       : FriendRequestsControllerIntegrationTests
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsRepositoryTests 메서드 단위/통합 테스트
 * - findByFromUser_UserIdAndToUser_UserIdAndStatus (파생 쿼리)
 * - findFriendsByUserIdAndStatus (JPQL @Query)
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
//Repository 테스트는 검증 대상이 쿼리 자체 이기 때문에 하나만함

@DataJpaTest(excludeAutoConfiguration = DataSourceInitializationAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaConfig.class)
@DisplayName("FriendRequestsRepository @Query 테스트")
class FriendRequestsRepositoryTests {

    @Autowired
    private FriendRequestsRepository friendRequestsRepository;

    @Autowired
    private UserRepository userRepository;

    private Users userA;
    private Users userB;

    @BeforeEach
    void setUp() {
        userA = userRepository.save(Users.builder()
                .email("a@naver.com").password("pw").nickname("유저A").build());
        userB = userRepository.save(Users.builder()
                .email("b@naver.com").password("pw").nickname("유저B").build());
    }

    private FriendRequests save(Users from, Users to, FriendRequestsStatus status) {
        FriendRequests req = new FriendRequests();
        req.setFromUser(from);
        req.setToUser(to);
        req.setStatus(status);
        req.setNickname(to.getNickname());
        return friendRequestsRepository.save(req);
    }

    // ===== findByFromUser_UserIdAndToUser_UserIdAndStatus =====

    @Test
    @DisplayName("findByFromUser_UserIdAndToUser_UserIdAndStatus - 일치하는 레코드 반환")
    void findByFromAndToAndStatus_found() {
        save(userA, userB, FriendRequestsStatus.PENDING);

        List<FriendRequests> result = friendRequestsRepository
                .findByFromUser_UserIdAndToUser_UserIdAndStatus(
                        userA.getUserId(), userB.getUserId(), FriendRequestsStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFromUser().getUserId()).isEqualTo(userA.getUserId());
        assertThat(result.get(0).getToUser().getUserId()).isEqualTo(userB.getUserId());
    }

    @Test
    @DisplayName("findByFromUser_UserIdAndToUser_UserIdAndStatus - status 불일치 시 빈 목록")
    void findByFromAndToAndStatus_statusMismatch() {
        save(userA, userB, FriendRequestsStatus.ACCEPTED);

        List<FriendRequests> result = friendRequestsRepository
                .findByFromUser_UserIdAndToUser_UserIdAndStatus(
                        userA.getUserId(), userB.getUserId(), FriendRequestsStatus.PENDING);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByFromUser_UserIdAndToUser_UserIdAndStatus - 역방향은 조회되지 않음")
    void findByFromAndToAndStatus_reverseNotFound() {
        save(userB, userA, FriendRequestsStatus.PENDING);

        List<FriendRequests> result = friendRequestsRepository
                .findByFromUser_UserIdAndToUser_UserIdAndStatus(
                        userA.getUserId(), userB.getUserId(), FriendRequestsStatus.PENDING);

        assertThat(result).isEmpty();
    }

    // ===== findFriendsByUserIdAndStatus (@Query JPQL) =====

    @Test
    @DisplayName("findFriendsByUserIdAndStatus - fromUser인 경우 조회")
    void findFriendsByUserIdAndStatus_asFromUser() {
        save(userA, userB, FriendRequestsStatus.ACCEPTED);

        List<FriendRequests> result = friendRequestsRepository
                .findFriendsByUserIdAndStatus(userA.getUserId(), FriendRequestsStatus.ACCEPTED);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findFriendsByUserIdAndStatus - toUser인 경우도 조회")
    void findFriendsByUserIdAndStatus_asToUser() {
        save(userA, userB, FriendRequestsStatus.ACCEPTED);

        List<FriendRequests> result = friendRequestsRepository
                .findFriendsByUserIdAndStatus(userB.getUserId(), FriendRequestsStatus.ACCEPTED);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findFriendsByUserIdAndStatus - PENDING 상태는 조회되지 않음")
    void findFriendsByUserIdAndStatus_statusMismatch() {
        save(userA, userB, FriendRequestsStatus.PENDING);

        List<FriendRequests> result = friendRequestsRepository
                .findFriendsByUserIdAndStatus(userA.getUserId(), FriendRequestsStatus.ACCEPTED);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findFriendsByUserIdAndStatus - 양방향 관계 모두 포함")
    void findFriendsByUserIdAndStatus_bothDirections() {
        Users userC = userRepository.save(Users.builder()
                .email("c@naver.com").password("pw").nickname("유저C").build());

        save(userA, userB, FriendRequestsStatus.ACCEPTED); // A -> B
        save(userC, userA, FriendRequestsStatus.ACCEPTED); // C -> A

        List<FriendRequests> result = friendRequestsRepository
                .findFriendsByUserIdAndStatus(userA.getUserId(), FriendRequestsStatus.ACCEPTED);

        assertThat(result).hasSize(2);
    }
}
