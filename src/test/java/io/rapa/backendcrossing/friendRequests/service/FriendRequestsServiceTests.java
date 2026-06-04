package io.rapa.backendcrossing.friendRequests.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.service
 * fileName       : FriendRequestsServiceTests
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsService 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayName("FriendRequestsService 단위 테스트")
public class FriendRequestsServiceTests {

    @InjectMocks
    private FriendRequestsService friendService;

    @Mock
    private FriendRequestsRepository friendRepository;
    @Mock
    private UserRepository userRepository;

    private Users userA;
    private Users userB;

    final Long userId = 1L;
    final Long targetId = 2L;
    final Long requestId = 10L;

    @BeforeEach
    void setUp() {
        userA = Users.builder()
                .email("test1@naver.com")
                .password("1234")
                .nickName("닉네임1")
                .build();
        ReflectionTestUtils.setField(userA, "userId", userId);

        userB = Users.builder()
                .email("test2@naver.com")
                .password("1234")
                .nickName("닉네임2")
                .build();
        ReflectionTestUtils.setField(userB, "userId", targetId);
    }

    // ===== getFriends =====

    @Test
    @DisplayName("친구 목록 조회 - 성공")
    void will_getFriends() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA);
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.ACCEPTED);

        when(friendRepository.findByFromUserUserIdOrToUserUserId(userId, userId))
                .thenReturn(List.of(request));

        var response = friendService.getFriends(userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(FriendRequestsStatus.ACCEPTED, response.get(0).getStatus());
    }

    @Test
    @DisplayName("친구 목록 조회 - userId null이면 예외")
    void will_getFriends_fail_nullUserId() {
        assertThrows(CustomException.class, () -> friendService.getFriends(null));
    }

    // ===== getReceivedRequests =====

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 성공")
    void will_getReceivedRequests() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.PENDING);

        when(friendRepository.findByToUserUserIdAndStatus(userId, FriendRequestsStatus.PENDING))
                .thenReturn(List.of(request));

        var response = friendService.getReceivedRequests(userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(FriendRequestsStatus.PENDING, response.get(0).getStatus());
    }

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - userId null이면 예외")
    void will_getReceivedRequests_fail_nullUserId() {
        assertThrows(CustomException.class, () -> friendService.getReceivedRequests(null));
    }

    // ===== sendFriendRequest =====

    @Test
    @DisplayName("친구 요청 전송 - 성공")
    void will_sendFriendRequest() {
        when(userRepository.findByIdOrThrow(userId)).thenReturn(userA);
        when(userRepository.findByIdOrThrow(targetId)).thenReturn(userB);
        when(friendRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(any(), any(), any())).thenReturn(false);
        when(friendRepository.existsByToUserUserIdAndFromUserUserIdAndStatus(any(), any(), any())).thenReturn(false);
        when(friendRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> friendService.sendFriendRequest(userId, targetId));

        verify(friendRepository, times(1)).save(any(FriendRequests.class));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (자기 자신에게 요청)")
    void will_sendFriendRequest_fail_self() {
        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, userId));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (이미 친구)")
    void will_sendFriendRequest_fail_alreadyFriend() {
        when(userRepository.findByIdOrThrow(userId)).thenReturn(userA);
        when(userRepository.findByIdOrThrow(targetId)).thenReturn(userB);
        when(friendRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.PENDING)).thenReturn(false);
        when(friendRepository.existsByToUserUserIdAndFromUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.PENDING)).thenReturn(false);
        when(friendRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.ACCEPTED)).thenReturn(true);

        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, targetId));
        verify(friendRepository, never()).save(any());
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (이미 대기 중인 요청)")
    void will_sendFriendRequest_fail_alreadyPending() {
        when(userRepository.findByIdOrThrow(userId)).thenReturn(userA);
        when(userRepository.findByIdOrThrow(targetId)).thenReturn(userB);
        when(friendRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.PENDING)).thenReturn(true);

        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, targetId));
        verify(friendRepository, never()).save(any());
    }

    // ===== acceptFriendRequest =====

    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void will_acceptFriendRequest_success() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.PENDING);

        when(friendRepository.findByFriendRequestIdAndToUserUserId(requestId, userId))
                .thenReturn(Optional.of(request));
        when(friendRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        var response = friendService.acceptFriendRequest(userId, requestId);

        assertNotNull(response);
        assertEquals(FriendRequestsStatus.ACCEPTED, response.getStatus());
        verify(friendRepository, times(1)).save(any(FriendRequests.class));
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (요청을 찾을 수 없음 404)")
    void will_acceptFriendRequest_fail_notFound() {
        when(friendRepository.findByFriendRequestIdAndToUserUserId(requestId, userId))
                .thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> friendService.acceptFriendRequest(userId, requestId));
        verify(friendRepository, never()).save(any());
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (이미 수락된 요청)")
    void will_acceptFriendRequest_fail_alreadyAccepted() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.ACCEPTED);

        when(friendRepository.findByFriendRequestIdAndToUserUserId(requestId, userId))
                .thenReturn(Optional.of(request));

        assertThrows(CustomException.class, () -> friendService.acceptFriendRequest(userId, requestId));
        verify(friendRepository, never()).save(any());
    }

    // ===== declineFriendRequest =====

    @Test
    @DisplayName("친구 요청 거절 - 성공")
    void will_declineFriendRequest_success() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.PENDING);

        when(friendRepository.findByFriendRequestIdAndToUserUserId(requestId, userId))
                .thenReturn(Optional.of(request));

        friendService.declineFriendRequest(userId, requestId);

        assertEquals(FriendRequestsStatus.DECLINED, request.getStatus());
    }

    @Test
    @DisplayName("친구 요청 거절 - 실패 (요청을 찾을 수 없음 404)")
    void will_declineFriendRequest_fail_notFound() {
        when(friendRepository.findByFriendRequestIdAndToUserUserId(requestId, userId))
                .thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> friendService.declineFriendRequest(userId, requestId));
        verify(friendRepository, never()).save(any());
    }

    // ===== cancelFriendRequest =====

    @Test
    @DisplayName("친구 요청 취소 - 성공")
    void will_cancelFriendRequest_success() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA);
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.PENDING);

        when(friendRepository.findByFriendRequestIdAndFromUserUserId(requestId, userId))
                .thenReturn(Optional.of(request));

        friendService.cancelFriendRequest(userId, requestId);

        assertEquals(FriendRequestsStatus.CANCELED, request.getStatus());
    }

    @Test
    @DisplayName("친구 요청 취소 - 실패 (요청을 찾을 수 없음 404)")
    void will_cancelFriendRequest_fail_notFound() {
        when(friendRepository.findByFriendRequestIdAndFromUserUserId(requestId, userId))
                .thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> friendService.cancelFriendRequest(userId, requestId));
    }

    // ===== deleteFriend =====

    @Test
    @DisplayName("친구 삭제 - 성공")
    void will_deleteFriend_success() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA);
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.ACCEPTED);

        when(friendRepository.findByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.ACCEPTED))
                .thenReturn(Optional.of(request));

        friendService.deleteFriend(userId, targetId);

        verify(friendRepository, times(1)).delete(request);
    }

    @Test
    @DisplayName("친구 삭제 - 성공 (역방향 친구 관계)")
    void will_deleteFriend_success_reverse() {
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.ACCEPTED);

        when(friendRepository.findByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.ACCEPTED))
                .thenReturn(Optional.empty());
        when(friendRepository.findByFromUserUserIdAndToUserUserIdAndStatus(targetId, userId, FriendRequestsStatus.ACCEPTED))
                .thenReturn(Optional.of(request));

        friendService.deleteFriend(userId, targetId);

        verify(friendRepository, times(1)).delete(request);
    }

    @Test
    @DisplayName("친구 삭제 - 실패 (친구 관계 없음 404)")
    void will_deleteFriend_fail_notFound() {
        when(friendRepository.findByFromUserUserIdAndToUserUserIdAndStatus(anyLong(), anyLong(), any()))
                .thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> friendService.deleteFriend(userId, targetId));
        assertEquals(ErrorCode.NOT_FRIEND_RELATION, ex.getErrorCode());
    }
}
