package io.rapa.backendcrossing.friendRequests.service;

import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@SpringBootTest //통합테스트용
@DisplayName("FriendRequests 서비스 통합 테스트")
@Transactional //db롤백용
@Slf4j
public class FriendRequestsServiceIntegrationTests {

    @Autowired
    private FriendRequestsService friendService;

    @Autowired
    private FriendRequestsRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Users userA;
    private Users userB;

    private Long userId;
    private Long targetId;

    @BeforeEach
    void setUp() {
        //userA = userRepository.save(UserUtils.makeUsers("test1@naver.com", passwordEncoder.encode("1234")));
        //userId = userA.getUserId();

        //userB = userRepository.save(UserUtils.makeUsers("test2@naver.com", passwordEncoder.encode("1234")));
        //targetId = userB.getUserId();

        userA = Users.builder()
                .email("test1@naver.com")
                .password("1234")
                .nickName("닉네임1")
                .build();

        userA = userRepository.save(userA);
        userId = userA.getUserId(); // DB가 생성한 실제 ID 할당

        userB = Users.builder()
                .email("test2@naver.com")
                .password("1234")
                .nickName("닉네임2")
                .build();
        userB = userRepository.save(userB);
        targetId = userB.getUserId();
    }

    @Test
    @DisplayName("친구 목록 조회 테스트")
    void will_getFriends() {
        // given: DB에 ACCEPTED 상태의 친구 관계 저장
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA);
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.ACCEPTED);

        request.setNickname(userA.getNickName());

        friendRepository.save(request);

        // when
        var response = friendService.getFriends(userId);

        // then
        assertNotNull(response);
        assertThat(response).hasSize(1);
        assertEquals(FriendRequestsStatus.ACCEPTED, response.get(0).getStatus());
    }

    @Test
    @DisplayName("받은 친구 요청 목록 조회 테스트")
    void will_getReceivedRequests() {
        // given: DB에 B가 A에게 보낸 PENDING 요청 저장
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.PENDING);
        request.setNickname(userB.getNickName());
        friendRepository.save(request);

        // when
        var response = friendService.getReceivedRequests(userId);

        // then
        assertNotNull(response);
        assertThat(response).hasSize(1);
        assertEquals(FriendRequestsStatus.PENDING, response.get(0).getStatus());
    }


    @Test
    @DisplayName("친구 요청 전송 테스트")
    void will_sendFriendRequest() {
        // given (Users are already saved in setUp)

        // when
        assertDoesNotThrow(() -> friendService.sendFriendRequest(userId, targetId));

        // then: 실제 DB에 PENDING 상태로 잘 저장되었는지 확인
        Optional<FriendRequests> savedRequest = friendRepository
                .findByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.PENDING);

        assertThat(savedRequest).isPresent();
        assertEquals(FriendRequestsStatus.PENDING, savedRequest.get().getStatus());
    }

    @Test
    @DisplayName("친구 요청 전송 실패 - 자기 자신에게 요청")
    void will_sendFriendRequest_fail_self() {
        assertThrows(CustomException.class, () -> friendService.sendFriendRequest(userId, userId));
    }

    @Test
    @DisplayName("친구 요청 전송 테스트 - 실패(이미 친구 또는 요청 중)")
    void will_sendFriendRequest_conflict() {
        // given: 이미 친구 관계인 데이터 저장
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA);
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.ACCEPTED);
        request.setNickname(userA.getNickName());
        friendRepository.save(request);

        // when & then: 다시 요청 시 예외 발생
        assertThrows(CustomException.class, () ->
                friendService.sendFriendRequest(userId, targetId)
        );
    }

    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void will_acceptFriendRequest_success() {
        // given: B가 A에게 보낸 요청을 DB에 저장
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);   // 내가(userA) 받은 요청
        request.setStatus(FriendRequestsStatus.PENDING);
        request.setNickname(userB.getNickName());
        FriendRequests savedRequest = friendRepository.save(request);
        Long requestId = savedRequest.getFriendRequestId(); // DB에 저장된 실제 요청 ID 획득

        // when
        var response = friendService.acceptFriendRequest(userId, requestId);

        // then: 상태가 ACCEPTED로 잘 반환되고, DB에도 업데이트 되었는지 확인
        assertEquals(FriendRequestsStatus.ACCEPTED, response.getStatus());

        FriendRequests updatedRequest = friendRepository.findById(requestId).orElseThrow();
        assertEquals(FriendRequestsStatus.ACCEPTED, updatedRequest.getStatus());
    }

    @Test
    @DisplayName("친구 요청 수락 실패 - 친구 요청을 찾을 수 없습니다. 404")
    void will_acceptFriendRequest_fail() {
        // given
        Long notFoundRequestId = 999999L; // 존재하지 않을 임의의 ID

        // when / then
        assertThrows(CustomException.class, () ->
                friendService.acceptFriendRequest(userId, notFoundRequestId)
        );
    }

    @Test
    @DisplayName("친구 요청 거절 성공")
    void will_rejectFriendRequest() {
        // given: B가 A에게 보낸 요청을 DB에 저장
        FriendRequests request = new FriendRequests();
        request.setFromUser(userB);
        request.setToUser(userA);
        request.setStatus(FriendRequestsStatus.PENDING);
        request.setNickname(userB.getNickName());
        FriendRequests savedRequest = friendRepository.save(request);
        Long requestId = savedRequest.getFriendRequestId();

        // when
        friendService.declineFriendRequest(userId, requestId);

        // then: DB 상태 확인
        FriendRequests updatedRequest = friendRepository.findById(requestId).orElseThrow();
        assertEquals(FriendRequestsStatus.DECLINED, updatedRequest.getStatus());
    }

    @Test
    @DisplayName("친구 요청 취소 성공")
    void will_cancelFriendRequest() {
        // given: A가 B에게 보낸 요청을 DB에 저장
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA); // A가 보냄
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.PENDING);
        request.setNickname(userA.getNickName());
        FriendRequests savedRequest = friendRepository.save(request);
        Long requestId = savedRequest.getFriendRequestId();

        // when
        friendService.cancelFriendRequest(userId, requestId);

        // then: DB 상태 확인
        FriendRequests updatedRequest = friendRepository.findById(requestId).orElseThrow();
        assertEquals(FriendRequestsStatus.CANCELED, updatedRequest.getStatus());
    }

    @Test
    @DisplayName("친구 삭제 성공")
    void will_deleteFriend() {
        // given: A와 B가 이미 친구(ACCEPTED) 상태
        FriendRequests request = new FriendRequests();
        request.setFromUser(userA);
        request.setToUser(userB);
        request.setStatus(FriendRequestsStatus.ACCEPTED);
        request.setNickname(userA.getNickName());
        friendRepository.save(request);

        // when: A가 B를 친구 삭제 (targetId 사용)
        friendService.deleteFriend(userId, targetId);

        // then: DB에서 해당 관계가 지워졌는지 확인 (isEmpty)
        Optional<FriendRequests> deletedRequest = friendRepository
                .findByFromUserUserIdAndToUserUserIdAndStatus(userId, targetId, FriendRequestsStatus.ACCEPTED);
        assertThat(deletedRequest).isEmpty();
    }

    @Test
    @DisplayName("친구 삭제 실패 - 친구를 찾을 수 없습니다 404")
    void will_deleteFriend_fail() {
        // given (No friends saved)

        // when & then
        assertThrows(CustomException.class, () ->
                friendService.deleteFriend(userId, targetId)
        );
    }

}
