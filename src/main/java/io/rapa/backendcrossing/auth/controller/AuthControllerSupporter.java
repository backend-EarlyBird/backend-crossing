package io.rapa.backendcrossing.auth.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.auth.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.auth.dto.request.AuthRefreshRequest;
import io.rapa.backendcrossing.auth.dto.response.AuthLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface AuthControllerSupporter {
    @Operation(
            summary = "이메일 로그인",
            description = "계정의 로그인을 수행하는 API"
    )
    @RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "email": "gamer@test.com",
                                      "password": "mypassword123"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "계정 로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                                    {
                                                      "success": true,
                                                      "message": "로그인되었습니다.",
                                                      "data": {
                                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                        "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
                                                        "accessExpiresInSeconds": 900
                                                      }
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "계정 로그인 실패 ( 이메일 / 비밀번호 올바르지 않음 )",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                     { 
                                        "success": false, 
                                        "message": "이메일 또는 비밀번호가 올바르지 않습니다.", 
                                        "data": null 
                                     }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "계정 로그인 실패 ( 서버 내부 오류 발생 )",
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
    ResponseEntity<CommonResponse<AuthLoginResponse>> logInAccount(AuthLoginRequest request);


    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token을 갱신하는 API"
    )
    @RequestBody(
            content = @Content(
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "토큰 갱신 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                                    {
                                                       "success": true,
                                                       "message": null,
                                                       "data": {
                                                         "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...NEW",
                                                         "refreshToken": "661f9511-f30c-52e5-b827-557766551111",
                                                         "accessExpiresInSeconds": 900
                                                       }
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "토큰 갱신 실패 ( 유효한 토큰 아님 )",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            """
                                         { 
                                            "success": false, 
                                            "message": "유효하지 않은 Refresh Token입니다.", 
                                            "data": null 
                                          }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "토큰 갱신 실패 ( 서버 내부 오류 발생 )",
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
    ResponseEntity<CommonResponse<AuthLoginResponse>> refreshToken(AuthRefreshRequest request);
}
