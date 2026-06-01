package io.rapa.backendcrossing.common.eventhandler;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.security.dto.KeyPair;
import io.rapa.backendcrossing.security.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    //
    @Value("${custom.baseUrl}")
    private String BASE_URL;
    //
    @Value("${custom.jwt.validations.access}")
    private Integer accessTokenTime;
    //
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CurrentUser principal;
        //
        if( authentication.getPrincipal() instanceof CurrentUser c ) principal = c;
        else{
            throw new CustomException(
                    ErrorCode.AUTHENTICATION_ERROR,
                    "Authentication 객체에서 형변환 중 오류가 발생했습니다."
            );
        }
        //
        KeyPair keyPair = tokenService.issueKeyPair(
                principal.getEmail(),
                principal.getRole()
        );
        //
        KeyPair issuedKeyPair = KeyPair.builder()
                .refreshToken(keyPair.refreshToken())
                .accessToken(keyPair.accessToken())
                .build();
        //
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        //
        objectMapper.writeValue(
                response.getWriter(),
                Map.of(
                        "accessToken", issuedKeyPair.accessToken(),
                        "refreshToken", issuedKeyPair.refreshToken(),
                        "accessExpiresInSeconds", accessTokenTime
                )
        );
    }
}
