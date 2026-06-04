package io.rapa.backendcrossing.friendRequests.controller;

import io.rapa.backendcrossing.common.constants.*;
import io.rapa.backendcrossing.friendRequests.reponse.*;
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
    @GetMapping(value = {"", "/"})
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> getFriends(
            @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.getFriends(currentUser.getId())));
    }

    // 받은 친구 요청 목록 조회
    @GetMapping("/requests")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> getReceivedRequests(
            @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.getReceivedRequests(currentUser.getId())));
    }

    // 친구 요청 전송
    @PostMapping("/requests")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> sendFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam Long toUserId) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.sendFriendRequest(currentUser.getId(), toUserId)));
    }

    // 친구 요청 수락
    @PatchMapping("/requests/{requestId}/accept")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> acceptFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.acceptFriendRequest(currentUser.getId(), requestId)));
    }

    // 친구 요청 거절
    @PatchMapping("/requests/{requestId}/decline")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> declineFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.declineFriendRequest(currentUser.getId(), requestId)));
    }

    // 친구 요청 취소
    @PatchMapping("/requests/{requestId}/cancel")
    public ResponseEntity<CommonResponse<FriendRequestResponse>> cancelFriendRequest(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(CommonResponse.success(
                friendRequestsService.cancelFriendRequest(currentUser.getId(), requestId)));
    }

    // 친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<CommonResponse<Void>> deleteFriend(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long friendId) {
        friendRequestsService.deleteFriend(currentUser.getId(), friendId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }
}
