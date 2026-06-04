package io.rapa.backendcrossing.friendRequests.controller;

import io.rapa.backendcrossing.common.constants.*;
import io.rapa.backendcrossing.friendRequests.reponse.*;
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

    //친구 목록 조회
    @GetMapping(value = {"", "/", })
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> getFriendRequest
            (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄
        Long userId = currentUser.getId();

        //수락된 친구 목록을 조회합니다. (status=ACCEPTED 인 요청 목록 반환)

        return ResponseEntity.ok(CommonResponse.success(null));

    }

    //받은 친구 요청 목록 조회
    @GetMapping("/requests")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> getFriendRequestRequests
    (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄
        Long userId = currentUser.getId();

        //내게 온 대기 중(PENDING) 친구 요청 목록을 조회합니다.   [인증: JWT Bearer Token 필요]
        return ResponseEntity.ok(CommonResponse.success(null));

    }

    //친구 요청 전송
    /*
    * {
          "toUserId": 8
        }
    * */
    @PostMapping("/requests")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> sendFriendRequest
            (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄
        Long userId = currentUser.getId();

        //상대방에게 친구 요청을 보냅니다.   [인증: JWT Bearer Token 필요]
        return ResponseEntity.ok(CommonResponse.success(null));

    }

    //친구 요청 수락
    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> acceptFriendRequest
    (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄
        Long userId = currentUser.getId();

        //받은 친구 요청을 수락합니다. 요청을 받은 사람만 가능합니다.   [인증: JWT Bearer Token 필요]


        return ResponseEntity.ok(CommonResponse.success(null));

    }

    //친구 요청 거절

    @PatchMapping("/requests/{requestId}/decline")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> rejectFriendRequest
    (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄
        Long userId = currentUser.getId();

        //받은 친구 요청을 거절합니다. 요청을 받은 사람만 가능합니다.   [인증: JWT Bearer Token 필요]
        return ResponseEntity.ok(CommonResponse.success(null));

    }

    //친구 요청 취소

    @PatchMapping("/requests/{requestId}")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> cancelFriendRequest
    (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄
        Long userId = currentUser.getId();

        //보낸 친구 요청을 취소합니다. 요청을 보낸 사람만 가능합니다.   [인증: JWT Bearer Token 필요]
        return ResponseEntity.ok(CommonResponse.success(null));

    }

    //친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<CommonResponse<List<FriendRequestResponse>>> deleteFriend
    (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄
        Long userId = currentUser.getId();

        //친구를 삭제합니다.   [인증: JWT Bearer Token 필요]
        return ResponseEntity.ok(CommonResponse.success(null));

    }



}
