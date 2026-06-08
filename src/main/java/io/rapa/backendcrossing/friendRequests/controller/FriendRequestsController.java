package io.rapa.backendcrossing.friendRequests.controller;

import io.rapa.backendcrossing.common.annotation.FriendRequestsSupperts;
import io.rapa.backendcrossing.common.constants.*;
import io.rapa.backendcrossing.friendRequests.reponse.*;
import io.rapa.backendcrossing.friendRequests.request.FriendRequestRequest;
import io.rapa.backendcrossing.friendRequests.service.FriendRequestsService;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * packageName    : io.rapa.backendcrossing.friends.controller
 * fileName       : FriendsController
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsController
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/friends")
@Tag(name = "FriendsController API", description = "친구 관련 API 명세서")
public class FriendRequestsController {

    private final FriendRequestsService friendRequestsService;

    // 친구 목록 조회
    @FriendRequestsSupperts.ApiGetFriends
    @GetMapping(value = {"", "/"})
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> getFriends(
            @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.getFriends(currentUser.getId())));
    }

    // 받은 친구 요청 목록 조회
    @FriendRequestsSupperts.ApiGetReceivedRequests
    @GetMapping("/requests")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> getReceivedRequests(
            @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.getReceivedRequests(currentUser.getId())));
    }

    // 친구 요청 전송
    @FriendRequestsSupperts.ApiSendFriendRequest
    @PostMapping("/requests")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> sendFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody FriendRequestRequest request) {
        return ResponseEntity.ok(CommonResponse.successWithMessage(
                friendRequestsService.sendFriendRequest(currentUser.getId(), request.getToUserId()),
                SuccessMessage.FRIEND_PENDING.getMessage()));
    }

    // 친구 요청 수락
    @FriendRequestsSupperts.ApiAcceptFriendRequest
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> acceptFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(CommonResponse.successWithMessage(
                friendRequestsService.acceptFriendRequest(currentUser.getId(), requestId),
                SuccessMessage.FRIEND_ACCEPTED.getMessage()));
    }

    // 친구 요청 거절
    @FriendRequestsSupperts.ApiDeclineFriendRequest
    @PostMapping("/requests/{requestId}/decline")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> declineFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(CommonResponse.successWithMessage(
                friendRequestsService.declineFriendRequest(currentUser.getId(), requestId),
                SuccessMessage.FRIEND_DECLINED.getMessage()));
    }

    // 친구 요청 취소
    @FriendRequestsSupperts.ApiCancelFriendRequest
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> cancelFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(CommonResponse.successWithMessage(
                friendRequestsService.cancelFriendRequest(currentUser.getId(), requestId),
                SuccessMessage.FRIEND_CANCELED.getMessage()));
    }

    // 친구 삭제
    @FriendRequestsSupperts.ApiDeleteFriend
    @DeleteMapping("/{friendId}")
    public ResponseEntity<CommonResponse<Void>> deleteFriend(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long friendId) {
        friendRequestsService.deleteFriend(currentUser.getId(), friendId);
        return ResponseEntity.ok(CommonResponse.successWithMessage(null,  SuccessMessage.FRIEND_DELETED.getMessage()));
    }
}
