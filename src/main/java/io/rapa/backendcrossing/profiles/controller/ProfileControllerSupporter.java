package io.rapa.backendcrossing.profiles.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.profiles.domain.dto.ProfileDetailResponse;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Profile API" , description = "프로필 관련 API 명세서")
public interface ProfileControllerSupporter {

    @Operation(
            summary = "프로필 조회",
            description = "계정의 프로필 조회를 수행하는 API"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "계정 프로필 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                                    {
                                                          "success": true,
                                                          "message": null,
                                                          "data": {
                                                            "level": 10,
                                                            "exp": 500,
                                                            "totalPlaySeconds": 7200
                                                          }
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "계정 프로필 조회 실패",
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
                            description = "계정 프로필 조회 실패 ( 서버 내부 오류 발생 )",
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
    ResponseEntity<CommonResponse<ProfileDetailResponse>> getProfile(CurrentUser currentUser);
}
