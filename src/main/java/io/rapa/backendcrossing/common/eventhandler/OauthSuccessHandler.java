package io.rapa.backendcrossing.common.eventhandler;

import io.rapa.backendcrossing.auth.domain.entity.UuidValidator;
import io.rapa.backendcrossing.auth.repository.UuidValidatorRepository;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.common.util.PreConditions;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UuidValidatorRepository uuidValidatorRepository;
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CurrentUser principal;
        HttpSession session = request.getSession();
        String redirect_uri = (String) session.getAttribute("redirect_uri");
        String state = (String) session.getAttribute("state");
        //
        if( authentication.getPrincipal() instanceof CurrentUser c ) principal = c;
        else{
            throw new CustomException(
                    ErrorCode.AUTHENTICATION_ERROR,
                    "Authentication 객체에서 형변환 중 오류가 발생했습니다."
            );
        }
        UuidValidator saved = uuidValidatorRepository.save(UuidValidator.builder().email(principal.getEmail()).build());
        getRedirectStrategy().sendRedirect(request,response,getUrlStr(saved, redirect_uri, state));
    }
    private String getUrlStr(UuidValidator validator, String uri, String state){
        return UriComponentsBuilder.fromUriString(uri)
                .queryParam("code", validator.getId())
                .queryParam("state", state)
                .build()
                .toUri()
                .toString();
    }
}
