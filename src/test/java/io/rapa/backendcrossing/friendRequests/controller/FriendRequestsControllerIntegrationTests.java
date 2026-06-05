package io.rapa.backendcrossing.friendRequests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.friendRequests.request.FriendRequestRequest;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.controller
 * fileName       : FriendRequestsControllerIntegrationTests
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsController 통합 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@DisplayName("FriendRequestsController 통합 테스트")
@Slf4j
public class FriendRequestsControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FriendRequestsRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String BASE_URL = "/api/v1/users/me/friends";

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

        CurrentUser currentUser = CurrentUser.builder()
                .email("test1@naver.com")
                .nickName("닉네임1")
                .build()
                .setId(userId)
                .setRole(Role.USER);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities())
        );
    }

    private FriendRequests saveFriendRequest(Users from, Users to, FriendRequestsStatus status) {
        FriendRequests req = new FriendRequests();
        req.setFromUser(from);
        req.setToUser(to);
        req.setStatus(status);
        req.setNickname(to.getNickname());
        return friendRepository.save(req);
    }

    // ===== GET /friends =====

    @Test
    @DisplayName("친구 목록 조회 - 성공")
    void getFriends_success() throws Exception {
        saveFriendRequest(userA, userB, FriendRequestsStatus.ACCEPTED);

        mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("친구 목록 조회 - 빈 목록")
    void getFriends_empty() throws Exception {
        mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ===== GET /friends/requests =====

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 성공")
    void getReceivedRequests_success() throws Exception {
        saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);

        mockMvc.perform(get(BASE_URL + "/requests").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 빈 목록")
    void getReceivedRequests_empty() throws Exception {
        mockMvc.perform(get(BASE_URL + "/requests").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ===== POST /friends/requests =====

    @Test
    @DisplayName("친구 요청 전송 - 성공")
    void sendFriendRequest_success() throws Exception {
        mockMvc.perform(post(BASE_URL + "/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new FriendRequestRequest(targetId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (자기 자신에게 요청 400)")
    void sendFriendRequest_fail_self() throws Exception {
        mockMvc.perform(post(BASE_URL + "/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new FriendRequestRequest(userId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.SELF_REQUEST.getDescription()));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (이미 친구 409)")
    void sendFriendRequest_fail_conflict() throws Exception {
        saveFriendRequest(userA, userB, FriendRequestsStatus.ACCEPTED);

        mockMvc.perform(post(BASE_URL + "/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new FriendRequestRequest(targetId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_FRIEND_OR_REQUESTED.getDescription()));
    }

    // ===== PATCH /friends/requests/{requestId}/accept =====

    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void acceptFriendRequest_success() throws Exception {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);

        mockMvc.perform(patch(BASE_URL + "/requests/" + saved.getFriendRequestId() + "/accept")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (요청 없음 404)")
    void acceptFriendRequest_fail_notFound() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/requests/999999/accept")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.REQUEST_NOT_FOUND.getDescription()));
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (이미 수락된 요청 400)")
    void acceptFriendRequest_fail_alreadyAccepted() throws Exception {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.ACCEPTED);

        mockMvc.perform(patch(BASE_URL + "/requests/" + saved.getFriendRequestId() + "/accept")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ===== PATCH /friends/requests/{requestId}/decline =====

    @Test
    @DisplayName("친구 요청 거절 - 성공")
    void declineFriendRequest_success() throws Exception {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);

        mockMvc.perform(patch(BASE_URL + "/requests/" + saved.getFriendRequestId() + "/decline")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DECLINED"));
    }

    @Test
    @DisplayName("친구 요청 거절 - 실패 (요청 없음 404)")
    void declineFriendRequest_fail_notFound() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/requests/999999/decline")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.REQUEST_NOT_FOUND.getDescription()));
    }

    // ===== PATCH /friends/requests/{requestId} =====

    @Test
    @DisplayName("친구 요청 취소 - 성공")
    void cancelFriendRequest_success() throws Exception {
        FriendRequests saved = saveFriendRequest(userB, userA, FriendRequestsStatus.PENDING);

        mockMvc.perform(delete(BASE_URL + "/requests/" + saved.getFriendRequestId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELED"));
    }

    @Test
    @DisplayName("친구 요청 취소 - 실패 (요청 없음 404)")
    void cancelFriendRequest_fail_notFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/requests/999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.REQUEST_NOT_FOUND.getDescription()));
    }

    // ===== DELETE /friends/{friendId} =====

    @Test
    @DisplayName("친구 삭제 - 성공")
    void deleteFriend_success() throws Exception {
        saveFriendRequest(userA, userB, FriendRequestsStatus.ACCEPTED);

        mockMvc.perform(delete(BASE_URL + "/" + targetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("친구 삭제 - 실패 (친구 관계 없음 404)")
    void deleteFriend_fail_notFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + targetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
