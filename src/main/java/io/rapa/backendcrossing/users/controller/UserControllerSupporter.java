package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;


@Tag(name = "Users" , description = "유저 관련 API")
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
    )
    ResponseEntity<CommonResponse<UserCreateResponse>> registerUser(UserCreateRequest request);
}
