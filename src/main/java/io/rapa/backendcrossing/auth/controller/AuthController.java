package io.rapa.backendcrossing.auth.controller;

import io.rapa.backendcrossing.auth.domain.dto.request.AuthGoogleExchangeRequest;
import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.security.domain.OauthHolderSingleton;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.auth.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.auth.domain.dto.request.AuthRefreshRequest;
import io.rapa.backendcrossing.auth.domain.dto.response.AuthLoginResponse;
import io.rapa.backendcrossing.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerSupporter{

    private final AuthService authService;

    @Value("${custom.jwt.validations.access}")
    private Integer ACCESS_TOKEN_EXPIRE_TIME;

    @Override
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> logInAccount(
            @Valid @RequestBody AuthLoginRequest request
    ){
        KeyPair keyPair = authService.signIn(request);
        return ResponseEntity.ok(
                CommonResponse.successWithMessage(
                        AuthLoginResponse.of(keyPair, ACCESS_TOKEN_EXPIRE_TIME / 1000),
                        SuccessMessage.LOGIN_SUCCESS.getMessage()
                )
        );
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> refreshToken(
            @Valid @RequestBody AuthRefreshRequest request
    ){
        KeyPair keyPair = authService.refreshToken(request);
        return ResponseEntity.ok(
                CommonResponse.successWithMessage(
                        AuthLoginResponse.of(keyPair, ACCESS_TOKEN_EXPIRE_TIME / 1000),
                        SuccessMessage.REFRESH_TOKEN_SUCCESS.getMessage()
                )
        );
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logOut(
            HttpServletRequest request
    ){
        authService.signOut(extractToken(request));
        return ResponseEntity.ok(
                CommonResponse.successWithMessage(
                        null,
                        SuccessMessage.LOGOUT_SUCCESS.getMessage()
                )
        );
    }

    @Override
    @GetMapping("/google/login")
    public void redirectToGoogle(
          @RequestParam(required = true) String redirect_uri,
          @RequestParam(required = true) String state,
          HttpServletResponse httpServletResponse
    ) throws IOException {
        OauthHolderSingleton instance = OauthHolderSingleton.getInstance();
        instance.setRedirect_uri(redirect_uri);
        instance.setState(state);
        httpServletResponse.sendRedirect("/oauth2/authorization/google");
    }

    @Override
    @PostMapping("/google/exchange")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> googleLoginAccount(
            @Valid @RequestBody AuthGoogleExchangeRequest request
    ) {
        KeyPair keyPair = authService.googleSignIn(request);
        return ResponseEntity.ok(
                CommonResponse.successWithMessage(
                        AuthLoginResponse.of(keyPair, ACCESS_TOKEN_EXPIRE_TIME / 1000),
                        ""
                )
        );
    }


    public String extractToken(HttpServletRequest request){
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 단락평가 이용하여  Bearer 제거 및 가공
        if(bearerToken != null && bearerToken.startsWith("Bearer")) return bearerToken.substring(7);
        return null;
    }
}
