package io.rapa.backendcrossing.friendRequests.service;

import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.service
 * fileName       : FriendRequestsServiceIntegrationTests
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsService 통합테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@SpringBootTest
@DisplayName("FriendRequests 서비스 통합 테스트")
@Transactional
@Slf4j
public class FriendRequestsServiceIntegrationTests {

    @Autowired
    private FriendRequestsService friendService;

    @Autowired
    private FriendRequestsRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private Users userA;
    private Users userB;

    private Long userId;
    private Long targetId;

    @BeforeEach
    void setUp() {
        userA = userRepository.save(Users.builder()
                .email("test1@naver.com")
                .password("1234")
                .nickname("닉네임1")
                .build());
        userId = userA.getUserId();

        userB = userRepository.save(Users.builder()
                .email("test2@naver.com")
                .password("1234")
                .nickname("닉네임2")
                .build());
        targetId = userB.getUserId();
    }

    private FriendRequests saveFriendRequest(Users from, Users to, FriendRequestsStatus status) {
        FriendRequests req = new FriendRequests();
        req.setFromUser(from);
        req.setToUser(to);
        req.setStatus(status);
        req.setNickname(to.getNickname());
        return friendRepository.save(req);
    }

    // ===== getFriends =====

    @Test
    @DisplayName("친구 목록 조회 - 성공 (fromUser가 나인 경우 - 상대방 nickname 반환)")
    void will_getFriends() {
        saveFriendRequest(userA, userB, FriendRequestsStatus.ACCEPTED);

        var response = friendService.getFriends(userId);

        assertThat(response).hasSize(1);
        assertEquals("닉네임2", response.get(0).getNickname());
    }

    @Test
    @DisplayName("친구 목록 조회 - 성공 (toUser가 나인 경우 - 상대방 nickname 반환)")
    void will_getFriends_asToUser() {
        saveFriendRequest(userB, userA, FriendRequestsStatus.ACCEPTED);

        var response = friendService.getFriends(userId);

        assertThat(response).hasSize(1);
        assertEquals("닉네임2", response.get(0).getNickname());
    }

    @Test
    @DisplayName("친구 목록 조회 - PENDING 상태는 포함되지 않음")
    void will_getFriends_excludesPending() {
        saveFriendRequest(userA, userB, FriendRequestsStatus.PENDING);

        var response = friendService.getFriends(userId);

        assertThat(response).isEmpty();
    }

    // ===== getReceivedRequests =====

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 성공")
    void will_getReceivedRequests() {
        saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);

        var response = friendService.getReceivedRequests(userId);

        assertNotNull(response);
        assertThat(response).hasSize(1);
        assertEquals(FriendRequestsStatus.PENDING, response.get(0).getStatus());
    }

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 없으면 빈 목록")
    void will_getReceivedRequests_empty() {
        var response = friendService.getReceivedRequests(userId);

        assertThat(response).isEmpty();
    }

    // ===== sendFriendRequest =====

    @Test
    @DisplayName("친구 요청 전송 - 성공")
    void will_sendFriendRequest() {
        assertDoesNotThrow(() -> friendService.sendFriendRequest(userId, targetId));

        Optional<FriendRequests> savedRequest = friendRepository
                .findByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.PENDING);

        assertThat(savedRequest).isPresent();
        assertEquals(FriendRequestsStatus.PENDING, savedRequest.get().getStatus());
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (자기 자신에게 요청)")
    void will_sendFriendRequest_fail_self() {
        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, userId));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (이미 친구 또는 요청 중)")
    void will_sendFriendRequest_fail_conflict() {
        saveFriendRequest(userA, userB, FriendRequestsStatus.ACCEPTED);

        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, targetId));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (이미 대기 중인 요청)")
    void will_sendFriendRequest_fail_alreadyPending() {
        saveFriendRequest(userA, userB, FriendRequestsStatus.PENDING);

        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, targetId));
    }

    // ===== acceptFriendRequest =====

    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void will_acceptFriendRequest_success() {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);
        Long requestId = saved.getFriendRequestId();

        var response = friendService.acceptFriendRequest(userId, requestId);

        assertEquals(FriendRequestsStatus.ACCEPTED, response.getStatus());
        assertEquals(FriendRequestsStatus.ACCEPTED, friendRepository.findById(requestId).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (요청을 찾을 수 없음 404)")
    void will_acceptFriendRequest_fail_notFound() {
        assertThrows(CustomException.class, () -> friendService.acceptFriendRequest(userId, 999999L));
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (이미 수락된 요청)")
    void will_acceptFriendRequest_fail_alreadyAccepted() {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.ACCEPTED);
        Long requestId = saved.getFriendRequestId();

        assertThrows(CustomException.class, () -> friendService.acceptFriendRequest(userId, requestId));
    }

    // ===== declineFriendRequest =====

    @Test
    @DisplayName("친구 요청 거절 - 성공")
    void will_declineFriendRequest_success() {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);
        Long requestId = saved.getFriendRequestId();

        friendService.declineFriendRequest(userId, requestId);

        assertEquals(FriendRequestsStatus.DECLINED, friendRepository.findById(requestId).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("친구 요청 거절 - 실패 (요청을 찾을 수 없음 404)")
    void will_declineFriendRequest_fail_notFound() {
        assertThrows(CustomException.class, () -> friendService.declineFriendRequest(userId, 999999L));
    }

    // ===== cancelFriendRequest =====

    @Test
    @DisplayName("친구 요청 취소 - 성공")
    void will_cancelFriendRequest_success() {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);
        Long requestId = saved.getFriendRequestId();

        friendService.cancelFriendRequest(userId, requestId);

        assertEquals(FriendRequestsStatus.CANCELED, friendRepository.findById(requestId).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("친구 요청 취소 - 실패 (요청을 찾을 수 없음 404)")
    void will_cancelFriendRequest_fail_notFound() {
        assertThrows(CustomException.class, () -> friendService.cancelFriendRequest(userId, 999999L));
    }

    // ===== deleteFriend =====

    @Test
    @DisplayName("친구 삭제 - 성공")
    void will_deleteFriend_success() {
        saveFriendRequest(userA, userB, FriendRequestsStatus.ACCEPTED);

        friendService.deleteFriend(userId, targetId);

        assertThat(friendRepository
                .findByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.ACCEPTED))
                .isEmpty();
    }

    @Test
    @DisplayName("친구 삭제 - 성공 (역방향 친구 관계)")
    void will_deleteFriend_success_reverse() {
        saveFriendRequest(userB, userA, FriendRequestsStatus.ACCEPTED);

        friendService.deleteFriend(userId, targetId);

        assertThat(friendRepository
                .findByFromUserUserIdAndToUserUserIdAndStatus(targetId, userId, FriendRequestsStatus.ACCEPTED))
                .isEmpty();
    }

    @Test
    @DisplayName("친구 삭제 - 실패 (친구 관계 없음 404)")
    void will_deleteFriend_fail_notFound() {
        assertThrows(CustomException.class, () -> friendService.deleteFriend(userId, targetId));
    }
}
