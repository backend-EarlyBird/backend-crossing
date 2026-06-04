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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

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

    // 테스트용 가짜 데이터
    private Users userA;
    private Users userB;

    Long userId = 1L;
    Long requestId = 2L;

    @BeforeEach
    void setUp() {
        userA = Users.builder()
                .email("test1@naver.com")
                .password("1234")
                .nickName("닉네임1")
                .build();
        ReflectionTestUtils.setField(userA, "userId", 1L); // 강제 주입

        userB = Users.builder()
                .email("test2@naver.com")
                .password("1234")
                .nickName("닉네임2")
                .build();
        ReflectionTestUtils.setField(userB, "userId", 2L); // 강제 주입
    }



    @Test
    @Transactional(readOnly = true)
    @DisplayName("친구 목록 조회 테스트")
    void will_getFriends(){
        // given
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA);
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.ACCEPTED);

        when(friendRepository.findByFromUserUserIdOrToUserUserId(userId, userId))
                .thenReturn(List.of(request));

        // when
        var response = friendService.getFriends(userId);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(FriendRequestsStatus.ACCEPTED, response.get(0).getStatus());
    }


    @Test
    @Transactional(readOnly = true)
    @DisplayName("받은 친구 요청 목록 조회 테스트")
    void will_getReceivedRequests(){
        // given
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.PENDING);

        when(friendRepository.findByToUserUserIdAndStatus(userId, FriendRequestsStatus.PENDING))
                .thenReturn(List.of(request));

        // when
        var response = friendService.getReceivedRequests(userId);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(FriendRequestsStatus.PENDING, response.get(0).getStatus());
    }



    //친구가 요청을 보냈을때
    @Test
    @DisplayName("친구 요청 전송 테스트")
    void will_sendFriendRequest(){
        //given
        when(userRepository.findByIdOrThrow(userId)).thenReturn(userA);
        when(userRepository.findByIdOrThrow(requestId)).thenReturn(userB);
        when(friendRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(any(), any(), any())).thenReturn(false);
        when(friendRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        //when
        assertDoesNotThrow(() -> friendService.sendFriendRequest(userId, requestId));

        //then
        //times() : 호출이 몇번되는지 확인용
        verify(friendRepository, times(1)).save(any(FriendRequests.class));
    }


    @Test
    @DisplayName("친구 요청 전송 실패 - 자기 자신에게 요청")
    void will_sendFriendRequest_fail_self() {
        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, userId));
    }


    //친구가 요청을 실패(이미 친구 관계입니다. / 이미 보낸 친구 요청이 있습니다. 409)
    @Test
    @DisplayName("친구 요청 전송 테스트 - 실패(이미 친구)")
    void will_sendFriendRequest_conflict(){
        when(userRepository.findByIdOrThrow(userId)).thenReturn(userA);
        when(userRepository.findByIdOrThrow(requestId)).thenReturn(userB);

        // 이미 친구인 상태를 true로 반환
        when(friendRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(userId, requestId, FriendRequestsStatus.PENDING)).thenReturn(false);
        when(friendRepository.existsByToUserUserIdAndFromUserUserIdAndStatus(userId, requestId, FriendRequestsStatus.PENDING)).thenReturn(false);
        when(friendRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(userId, requestId, FriendRequestsStatus.ACCEPTED)).thenReturn(true);

        // when & then
        assertThrows(CustomException.class, () ->
                friendService.sendFriendRequest(userId, requestId)
        );

        // save는 절대로 호출되지 않아야 함
        verify(friendRepository, never()).save(any());
    }

    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void will_acceptFriendRequest_success() {
        // given
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB); //
        request.setToUser(userA);   // 내가(userA) 받은 요청이어야 수락 가능
        request.setStatus(FriendRequestsStatus.PENDING); // 대기 상태여야 함

        when(friendRepository.findByFriendRequestIdAndToUserUserId(requestId, userId))
                .thenReturn(Optional.of(request));
        when(friendRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // when
        var response = friendService.acceptFriendRequest(userId, requestId);

        // then
        assertNotNull(response);
        assertEquals(FriendRequestsStatus.ACCEPTED, response.getStatus());
        verify(friendRepository, times(1)).save(any(FriendRequests.class));
    }


    @Test
    @DisplayName("친구 요청 수락 실패 - 자기 자신에게 요청")
    void will_acceptFriendRequest_fail_self(){
        assertThrows(CustomException.class, () -> friendService.acceptFriendRequest(userId, userId));
    }


    // 친구 요청 수락 실패(친구 요청을 찾을 수 없습니다. 404)
    @Test
    @DisplayName("친구 요청 수락 실패 - 친구 요청을 찾을 수 없습니다. 404")
    void will_acceptFriendRequest_fail(){
        // Given
        Long notFoundRequestId = 999L;

        when(friendRepository.findByFriendRequestIdAndToUserUserId(notFoundRequestId, userId))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(CustomException.class, () ->
                friendService.acceptFriendRequest(userId, notFoundRequestId)
        );

        verify(friendRepository, never()).save(any());
    }


    // 친구 요청 거절
    @Test
    @DisplayName("친구 요청 거절 성공")
    void will_rejectFriendRequest(){
        // Given
        FriendRequests request = new FriendRequests();
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.PENDING);

        when(friendRepository.findByFriendRequestIdAndToUserUserId(requestId, userId))
                .thenReturn(Optional.of(request));

        // When
        friendService.declineFriendRequest(userId, requestId);

        // Then
        assertEquals(FriendRequestsStatus.DECLINED, request.getStatus());
    }

    @Test
    @DisplayName("친구 요청 거절 실패 - 자기 자신에게 요청")
    void will_rejectFriendRequest_fail_self(){
        assertThrows(CustomException.class, () -> friendService.declineFriendRequest(userId, userId));
    }

    // 친구 요청 거절 실패(친구 요청을 찾을 수 없습니다 404)
    @Test
    @DisplayName("친구 요청 거절 실패 - 요청을 찾을 수 없습니다 404")
    void will_rejectFriendRequest_fail(){
        // given
        Long notFoundRequestId = 999L;
        when(friendRepository.findByFriendRequestIdAndToUserUserId(notFoundRequestId, userId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(CustomException.class, () ->
                friendService.declineFriendRequest(userId, notFoundRequestId)
        );

        verify(friendRepository, never()).save(any());
    }

    // 친구 요청 취소
    @Test
    @DisplayName("친구 요청 취소 성공")
    void will_cancelFriendRequest(){
        // given
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA); // 취소는 요청을 보낸 사람이 함
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.PENDING);

        when(friendRepository.findByFriendRequestIdAndFromUserUserId(requestId, userId))
                .thenReturn(Optional.of(request));

        // when
        friendService.cancelFriendRequest(userId, requestId);

        // then
        assertEquals(FriendRequestsStatus.CANCELED, request.getStatus());
    }

    // 친구 요청 취소 실패(친구 요청을 찾을 수 없습니다 404)
    @Test
    @DisplayName("친구 요청 취소 실패 - 요청을 찾을 수 없습니다 404")
    void will_cancelFriendRequest_fail(){
        // given
        Long notFoundRequestId = 999L;
        when(friendRepository.findByFriendRequestIdAndFromUserUserId(notFoundRequestId, userId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () ->
                friendService.cancelFriendRequest(userId, notFoundRequestId)
        );
    }

    // 친구 삭제
    @Test
    @DisplayName("친구 삭제 성공")
    void will_deleteFriend(){
        // given
        FriendRequests request = new FriendRequests();
        request.setStatus(FriendRequestsStatus.ACCEPTED);

        when(friendRepository.findByFromUserUserIdAndToUserUserIdAndStatus(userId, requestId, FriendRequestsStatus.ACCEPTED))
                .thenReturn(Optional.of(request));

        // when
        friendService.deleteFriend(userId, requestId);

        // then
        verify(friendRepository, times(1)).delete(request);
    }

    // 친구 삭제(친구를 찾을 수 없습니다 404)
    @Test
    @DisplayName("친구 삭제 실패 - 친구를 찾을 수 없습니다 404")
    void will_deleteFriend_fail(){
        // given
        when(friendRepository.findByFromUserUserIdAndToUserUserIdAndStatus(anyLong(), anyLong(), any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () ->
                friendService.deleteFriend(userId, requestId)
        );
    }
}