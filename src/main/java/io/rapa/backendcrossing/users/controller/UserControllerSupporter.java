package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.MeDetailResponse;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User API" , description = "유저 관련 API 명세서")
public interface UserControllerSupporter {
    @Operation(
            summary = "회원가입",
            description = "계정의 회원가입을 수행하는 API"
    )
    @RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "email": "newgamer@test.com",
                                      "password": "mypassword123",
                                      "nickname": "새싹게이머"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원가입 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                                    {
                                                              "success": true,
                                                              "message": "가입되었습니다.",
                                                              "data": {
                                                                "userId": 5,
                                                                "email": "newgamer@test.com",
                                                                "nickname": "새싹게이머",
                                                                "role": "USER",
                                                                "status": "ACTIVE",
                                                                "provider": "LOCAL",
                                                                "profileImageUrl": null,
                                                                "createdAt": "2026-05-21T10:30:00",
                                                                "lastLoginAt": null
                                                              }
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "회원가입 실패 ( 비밀번호 조건 오류 )",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                                { 
                                                    "success": false, 
                                                    "message": "비밀번호는 8~64자여야 합니다.", 
                                                    "data": null 
                                                }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "회원가입 실패 ( 이메일 이미 존재 )",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                { 
                                    "success": false, 
                                    "message": "이미 사용 중인 이메일입니다.", 
                                    "data": null 
                                }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "회원가입 실패 ( 서버 내부 오류 발생 )",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                { 
                                    "success": false, 
                                    "message": "서버 오류가 발생했습니다.", 
                                    "data": null 
                                }
                                            """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<CommonResponse<UserCreateResponse>> registerUser(UserCreateRequest request);


    @Operation(
            summary = "내 계정 정보 조회",
            description = "현재 로그인한 유저의 계정 정보를 조회하는 API",
            security = {
                    @SecurityRequirement(name = "Authorization Header : Bearer Token")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                                    {
                                                      "success": true,
                                                      "message": null,
                                                      "data": {
                                                        "userId": 5,
                                                        "email": "gamer@test.com",
                                                        "nickname": "게이머",
                                                        "role": "USER",
                                                        "status": "ACTIVE",
                                                        "provider": "LOCAL",
                                                        "profileImageUrl": null,
                                                        "createdAt": "2025-01-01T00:00:00",
                                                        "lastLoginAt": "2026-05-21T10:00:00"
                                                      }
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "조회 실패 ( 인증 필요 )",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                            { 
                                                "success": false, 
                                                "message": "인증이 필요합니다.", 
                                                "data": null 
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "조회 실패 ( 서버 내부 오류 발생 )",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                { 
                                    "success": false, 
                                    "message": "서버 오류가 발생했습니다.", 
                                    "data": null 
                                }
                                            """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<CommonResponse<MeDetailResponse>> getMeDetails(CurrentUser currentUser);
}
