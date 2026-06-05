package io.rapa.backendcrossing.friendRequests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.reponse.FriendRequestResponse;
import io.rapa.backendcrossing.friendRequests.service.FriendRequestsService;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.controller
 * fileName       : FriendRequestsControllerTests
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsController 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@WebMvcTest(FriendRequestsController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("FriendRequestsController 단위 테스트")
@Slf4j
public class FriendRequestsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendRequestsService friendRequestsService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserService userService;

    private static final String BASE_URL = "/api/v1/users/me/friends";
    private static final Long USER_ID = 1L;
    private static final Long TARGET_ID = 2L;
    private static final Long REQUEST_ID = 10L;

    @BeforeEach
    void setUpSecurityContext() {
        CurrentUser currentUser = CurrentUser.builder()
                .email("test@test.com")
                .nickName("테스터")
                .build()
                .setId(USER_ID)
                .setRole(Role.USER);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities())
        );
    }

    // ===== getFriends =====

    @Test
    @DisplayName("친구 목록 조회 - 성공")
    void getFriends_success() throws Exception {
        FriendRequestResponse response = new FriendRequestResponse();
        response.setStatus(FriendRequestsStatus.ACCEPTED);

        given(friendRequestsService.getFriends(USER_ID)).willReturn(List.of(response));

        mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("친구 목록 조회 - 빈 목록")
    void getFriends_empty() throws Exception {
        given(friendRequestsService.getFriends(USER_ID)).willReturn(List.of());

        mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ===== getReceivedRequests =====

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 성공")
    void getReceivedRequests_success() throws Exception {
        FriendRequestResponse response = new FriendRequestResponse();
        response.setStatus(FriendRequestsStatus.PENDING);

        given(friendRequestsService.getReceivedRequests(USER_ID)).willReturn(List.of(response));

        mockMvc.perform(get(BASE_URL + "/requests").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("받은 친구 요청 목록 조회 - 빈 목록")
    void getReceivedRequests_empty() throws Exception {
        given(friendRequestsService.getReceivedRequests(USER_ID)).willReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/requests").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ===== sendFriendRequest =====

    @Test
    @DisplayName("친구 요청 전송 - 성공")
    void sendFriendRequest_success() throws Exception {
        FriendRequestResponse response = new FriendRequestResponse();
        response.setStatus(FriendRequestsStatus.PENDING);

        given(friendRequestsService.sendFriendRequest(USER_ID, TARGET_ID)).willReturn(response);

        mockMvc.perform(post(BASE_URL + "/requests")
                        .param("toUserId", String.valueOf(TARGET_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (자기 자신에게 요청 400)")
    void sendFriendRequest_fail_self() throws Exception {
        given(friendRequestsService.sendFriendRequest(USER_ID, USER_ID))
                .willThrow(new CustomException(ErrorCode.SELF_REQUEST));

        mockMvc.perform(post(BASE_URL + "/requests")
                        .param("toUserId", String.valueOf(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.SELF_REQUEST.getDescription()));
    }

    @Test
    @DisplayName("친구 요청 전송 - 실패 (이미 친구 409)")
    void sendFriendRequest_fail_conflict() throws Exception {
        given(friendRequestsService.sendFriendRequest(USER_ID, TARGET_ID))
                .willThrow(new CustomException(ErrorCode.ALREADY_FRIEND_OR_REQUESTED));

        mockMvc.perform(post(BASE_URL + "/requests")
                        .param("toUserId", String.valueOf(TARGET_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_FRIEND_OR_REQUESTED.getDescription()));
    }

    // ===== acceptFriendRequest =====

    @Test
    @DisplayName("친구 요청 수락 - 성공")
    void acceptFriendRequest_success() throws Exception {
        FriendRequestResponse response = new FriendRequestResponse();
        response.setStatus(FriendRequestsStatus.ACCEPTED);

        given(friendRequestsService.acceptFriendRequest(USER_ID, REQUEST_ID)).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/requests/" + REQUEST_ID + "/accept")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (요청 없음 404)")
    void acceptFriendRequest_fail_notFound() throws Exception {
        given(friendRequestsService.acceptFriendRequest(USER_ID, REQUEST_ID))
                .willThrow(new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        mockMvc.perform(patch(BASE_URL + "/requests/" + REQUEST_ID + "/accept")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.REQUEST_NOT_FOUND.getDescription()));
    }

    @Test
    @DisplayName("친구 요청 수락 - 실패 (잘못된 수락 400)")
    void acceptFriendRequest_fail_invalid() throws Exception {
        given(friendRequestsService.acceptFriendRequest(USER_ID, REQUEST_ID))
                .willThrow(new CustomException(ErrorCode.INVALID_ACCEPT));

        mockMvc.perform(patch(BASE_URL + "/requests/" + REQUEST_ID + "/accept")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_ACCEPT.getDescription()));
    }

    // ===== declineFriendRequest =====

    @Test
    @DisplayName("친구 요청 거절 - 성공")
    void declineFriendRequest_success() throws Exception {
        FriendRequestResponse response = new FriendRequestResponse();
        response.setStatus(FriendRequestsStatus.DECLINED);

        given(friendRequestsService.declineFriendRequest(USER_ID, REQUEST_ID)).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/requests/" + REQUEST_ID + "/decline")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DECLINED"));
    }

    @Test
    @DisplayName("친구 요청 거절 - 실패 (요청 없음 404)")
    void declineFriendRequest_fail_notFound() throws Exception {
        given(friendRequestsService.declineFriendRequest(USER_ID, REQUEST_ID))
                .willThrow(new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        mockMvc.perform(patch(BASE_URL + "/requests/" + REQUEST_ID + "/decline")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.REQUEST_NOT_FOUND.getDescription()));
    }

    // ===== cancelFriendRequest =====

    @Test
    @DisplayName("친구 요청 취소 - 성공")
    void cancelFriendRequest_success() throws Exception {
        FriendRequestResponse response = new FriendRequestResponse();
        response.setStatus(FriendRequestsStatus.CANCELED);

        given(friendRequestsService.cancelFriendRequest(USER_ID, REQUEST_ID)).willReturn(response);

        mockMvc.perform(patch(BASE_URL + "/requests/" + REQUEST_ID + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELED"));
    }

    @Test
    @DisplayName("친구 요청 취소 - 실패 (요청 없음 404)")
    void cancelFriendRequest_fail_notFound() throws Exception {
        given(friendRequestsService.cancelFriendRequest(USER_ID, REQUEST_ID))
                .willThrow(new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        mockMvc.perform(patch(BASE_URL + "/requests/" + REQUEST_ID + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.REQUEST_NOT_FOUND.getDescription()));
    }

    // ===== deleteFriend =====

    @Test
    @DisplayName("친구 삭제 - 성공")
    void deleteFriend_success() throws Exception {
        doNothing().when(friendRequestsService).deleteFriend(USER_ID, TARGET_ID);

        mockMvc.perform(delete(BASE_URL + "/" + TARGET_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("친구 삭제 - 실패 (친구 관계 없음 404)")
    void deleteFriend_fail_notFound() throws Exception {
        doThrow(new CustomException(ErrorCode.NOT_FRIEND_RELATION))
                .when(friendRequestsService).deleteFriend(USER_ID, TARGET_ID);

        mockMvc.perform(delete(BASE_URL + "/" + TARGET_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FRIEND_RELATION.getDescription()));
    }
}
