package io.rapa.backendcrossing.oauth.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.oauth.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(
            OAuth2UserRequest userRequest
    ) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerId = userRequest.getClientRegistration().getRegistrationId();
        if (providerId.equals("google")) providerId = "gmail";

        String extractedEmail = oAuth2User.getAttributes().get("email").toString();

        Users foundedUser = userRepository.findByEmail(extractedEmail).orElseThrow(
                ()-> new OAuth2AuthenticationException("해당 이메일의 계정은 가입되어 있지 않습니다.")
        );
        
        if ( !providerId.equals(foundedUser.getProvider()) ){
            throw new OAuth2AuthenticationException("계정과 다른 이메일 도메인의 서비스로 로그인이 불가능합니다.");
        }

        return CurrentUser.from(foundedUser);
    }
}
