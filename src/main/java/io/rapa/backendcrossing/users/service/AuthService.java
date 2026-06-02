package io.rapa.backendcrossing.users.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.util.PreConditions;
import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import io.rapa.backendcrossing.users.constants.UserStatus;
import io.rapa.backendcrossing.users.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

        return tokenService.issueKeyPair(
                foundedUser.getEmail(),
                foundedUser.getRole()
        );
    }
}
