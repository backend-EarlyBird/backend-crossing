package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.users.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.users.domain.dto.request.AuthRefreshRequest;
import io.rapa.backendcrossing.users.domain.dto.response.AuthLoginResponse;
import io.rapa.backendcrossing.users.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerSupporter{

    private final AuthService authService;

    @Value("${custom.jwt.validations.access}")
    private Integer ACCESS_TOKEN_EXPIRE_TIME;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> logInAccount(
            @Valid @RequestBody AuthLoginRequest request
    ){
        KeyPair keyPair = authService.signIn(request);
        return ResponseEntity.ok(
                CommonResponse.successWithMessage(
                        AuthLoginResponse.of(keyPair, ACCESS_TOKEN_EXPIRE_TIME),
                        SuccessMessage.LOGIN_SUCCESS.getMessage()
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> refreshToken(
            @Valid @RequestBody AuthRefreshRequest request
    ){
        KeyPair keyPair = authService.refreshToken(request);
        return ResponseEntity.ok(
                CommonResponse.successWithMessage(
                        AuthLoginResponse.of(keyPair, ACCESS_TOKEN_EXPIRE_TIME),
                        SuccessMessage.REFRESH_TOKEN_SUCCESS.getMessage()
                )
        );
    }
}
