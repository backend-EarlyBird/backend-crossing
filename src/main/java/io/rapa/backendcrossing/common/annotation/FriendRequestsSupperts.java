package io.rapa.backendcrossing.common.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * packageName    : io.rapa.backendcrossing.common.annotation
 * fileName       : FriendRequestsSupperts
 * author         : Admin
 * date           : 26. 6. 5.
 * description    : FriendRequestsController Swagger 설명용 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 5.        Admin       최초 생성
 */
public interface FriendRequestsSupperts {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "친구 목록 조회", description = "현재 유저의 친구 목록을 조회합니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": true, "message": null, "data": [{ "friendRequestId": 1, "fromUser": {}, "toUser": {}, "status": "ACCEPTED", "createdAt": "2026-06-05T00:00:00" }] }
                    """)))
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"인증이 필요합니다.\", \"data\": null }")))
    public @interface ApiGetFriends {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "받은 친구 요청 목록 조회", description = "나에게 온 친구 요청 목록을 조회합니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": true, "message": null, "data": [{ "friendRequestId": 2, "fromUser": {}, "toUser": {}, "status": "PENDING", "createdAt": "2026-06-05T00:00:00" }] }
                    """)))
    @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"인증이 필요합니다.\", \"data\": null }")))
    public @interface ApiGetReceivedRequests {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "친구 요청 전송", description = "특정 유저에게 친구 요청을 전송합니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth"))
    @RequestBody(description = "친구 요청 대상 유저 ID",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("1")))
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": true, "message": null, "data": { "friendRequestId": 3, "fromUser": {}, "toUser": {}, "status": "PENDING", "createdAt": "2026-06-05T00:00:00" } }
                    """)))
    @ApiResponse(responseCode = "400", description = "잘못된 요청",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"이미 친구 요청을 보냈습니다.\", \"data\": null }")))
    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"유저를 찾을 수 없습니다.\", \"data\": null }")))
    public @interface ApiSendFriendRequest {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "친구 요청 수락", description = "받은 친구 요청을 수락합니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": true, "message": null, "data": { "friendRequestId": 2, "fromUser": {}, "toUser": {}, "status": "ACCEPTED", "createdAt": "2026-06-05T00:00:00" } }
                    """)))
    @ApiResponse(responseCode = "403", description = "권한 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"해당 요청에 대한 권한이 없습니다.\", \"data\": null }")))
    @ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"친구 요청을 찾을 수 없습니다.\", \"data\": null }")))
    public @interface ApiAcceptFriendRequest {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "친구 요청 거절", description = "받은 친구 요청을 거절합니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": true, "message": null, "data": { "friendRequestId": 2, "fromUser": {}, "toUser": {}, "status": "DECLINED", "createdAt": "2026-06-05T00:00:00" } }
                    """)))
    @ApiResponse(responseCode = "403", description = "권한 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"해당 요청에 대한 권한이 없습니다.\", \"data\": null }")))
    @ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"친구 요청을 찾을 수 없습니다.\", \"data\": null }")))
    public @interface ApiDeclineFriendRequest {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "친구 요청 취소", description = "내가 보낸 친구 요청을 취소합니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": true, "message": null, "data": { "friendRequestId": 3, "fromUser": {}, "toUser": {}, "status": "CANCELLED", "createdAt": "2026-06-05T00:00:00" } }
                    """)))
    @ApiResponse(responseCode = "403", description = "권한 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"해당 요청에 대한 권한이 없습니다.\", \"data\": null }")))
    @ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"친구 요청을 찾을 수 없습니다.\", \"data\": null }")))
    public @interface ApiCancelFriendRequest {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": true, \"message\": null, \"data\": null }")))
    @ApiResponse(responseCode = "403", description = "권한 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"해당 요청에 대한 권한이 없습니다.\", \"data\": null }")))
    @ApiResponse(responseCode = "404", description = "친구를 찾을 수 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"친구를 찾을 수 없습니다.\", \"data\": null }")))
    public @interface ApiDeleteFriend {}
}
