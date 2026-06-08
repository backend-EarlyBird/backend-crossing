package io.rapa.backendcrossing.wallets.controller;


import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.wallets.domain.dto.WalletDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Wallet API" , description = "지갑 관련 API 명세서")
public interface WalletControllerSupporter {

    @Operation(
            summary = "지갑 조회",
            description = "현재 로그인한 유저의 골드와 보석 잔액을 조회하는 API",
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
                                                                 "gold": 5000,
                                                                 "gem": 10
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
                            description = "지갑조회 실패 ( 서버 내부 오류 발생 )",
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
    ResponseEntity<CommonResponse<WalletDetailResponse>> getWalletDetail(CurrentUser currentUser);

}
