package io.rapa.backendcrossing.users.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.MeDetailResponse;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepository;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepositoryImpl;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class UserService {
    private final UserBoundaryRepository userBoundaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserBoundaryRepositoryImpl userBoundaryRepositoryImpl;

    @Transactional
    public UserCreateResponse registerUser(UserCreateRequest request){

        Optional<Users> foundedUser = userBoundaryRepository.findUserByEmail(request.email());

        if (foundedUser.isPresent()) throw new CustomException(ErrorCode.EMAIL_ALREDY_EXISTS);

        Users savedUser = userBoundaryRepository.saveUser(
                Users.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .nickname(request.nickname())
                        .build()
        );

        userBoundaryRepository.saveWallet(
                Wallets.builder()
                        .gem(0L).gold(0L).user(savedUser)
                        .build()
        );
        userBoundaryRepository.saveProfile(
                Profiles.builder()
                        .level(0).exp(0L).user(savedUser).totalPlaySeconds(0L)
                        .build()
        );


        return UserCreateResponse.from(savedUser);
    }

    public CurrentUser loadCurrentUserByEmail(String email){
        return CurrentUser.from(
                userBoundaryRepository.findUserByEmailOrThrow(email)
        );
    }

    @PreAuthorize("#userEmail == authentication.principal.email and isAuthenticated()")
    public MeDetailResponse getDetailofMe(String userEmail){
        Users founded = userBoundaryRepository.findUserByEmailOrThrow(userEmail);
        return MeDetailResponse.from(founded);
    }


}
