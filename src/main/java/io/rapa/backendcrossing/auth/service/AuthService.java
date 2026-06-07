package io.rapa.backendcrossing.auth.service;

import io.rapa.backendcrossing.auth.domain.dto.request.AuthGoogleExchangeRequest;
import io.rapa.backendcrossing.auth.domain.dto.response.AuthLoginResponse;
import io.rapa.backendcrossing.auth.domain.entity.UuidValidator;
import io.rapa.backendcrossing.auth.repository.UuidValidatorRepository;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.common.util.PreConditions;
import io.rapa.backendcrossing.security.domain.RefreshToken;
import io.rapa.backendcrossing.security.repository.RefreshTokenRepository;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.users.constants.UserStatus;
import io.rapa.backendcrossing.auth.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.auth.domain.dto.request.AuthRefreshRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
@Transactional(readOnly = true)
public class AuthService {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UuidValidatorRepository uuidValidatorRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public KeyPair signIn(AuthLoginRequest authLoginRequest){
        Users foundedUser = userRepository.findByEmailOrThrow(authLoginRequest.email());

        PreConditions.validate(
                passwordEncoder.matches(authLoginRequest.password(), foundedUser.getPassword()),
                ErrorCode.PASSWORD_INCORRECT
        );

        PreConditions.validate(
                foundedUser.getUserStatus().equals(UserStatus.ACTIVATED),
                ErrorCode.USER_INACTIVATED
        );

        foundedUser.setLoginTimeNow();

        return tokenService.issueKeyPair(
                foundedUser.getEmail(),
                foundedUser.getRole()
        );
    }

    @Transactional
    public KeyPair refreshToken(AuthRefreshRequest authRefreshRequest){
        String receivedRT = authRefreshRequest.refreshToken();

        PreConditions.validate(
                receivedRT != null,
                ErrorCode.TOKEN_NOT_FOUND
        );

        try{
            PreConditions.validate(
                    tokenService.validate(receivedRT),
                    ErrorCode.ABNORMAL_REFRESH_TOKEN
            );
        } catch (CustomException e){
            if( e.getErrorCode().equals(ErrorCode.ABNORMAL_TOKEN) ) throw new CustomException(ErrorCode.ABNORMAL_REFRESH_TOKEN);
        }



        RefreshToken foundedRT = refreshTokenRepository.findRefreshTokenByRefreshToken(receivedRT)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
                );

        refreshTokenRepository.delete(foundedRT);

        Users foundedUser = userRepository.findByEmailOrThrow(foundedRT.getEmail());

        return tokenService.issueKeyPair(
                foundedUser.getEmail(),
                foundedUser.getRole()
        );
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void signOut(String refreshToken){
        if(Strings.isBlank(refreshToken)) throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        refreshTokenRepository.deleteById(refreshToken);
    }

    @Transactional
    public KeyPair googleSignIn(AuthGoogleExchangeRequest request){
        String code = UUID.fromString(request.code()).toString();

        PreConditions.validate(
            uuidValidatorRepository.existsById(code),
            ErrorCode.NO_GOOGLE_AUTH_HISTORY
        );

        UuidValidator uuidValidator = uuidValidatorRepository.findByIdOrThrow(code);

        Users foundedUser = userRepository.findByEmailOrThrow(uuidValidator.getUserEmail());

        return tokenService.issueKeyPair(
                foundedUser.getEmail(),
                foundedUser.getRole()
        );
    }
}
