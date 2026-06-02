package io.rapa.backendcrossing.users.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserCreateResponse registerUser(UserCreateRequest request){

        Optional<Users> foundedUser = userRepository.findByEmail(request.email());

        if (foundedUser.isPresent()) throw new CustomException(ErrorCode.EMAIL_ALREAY_EXISTS);

        Users savedUser = userRepository.save(
                Users.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .nickName(request.nickname())
                        .build()
        );
        return UserCreateResponse.from(savedUser);
    }

    public CurrentUser loadCurrentUserByEmail(String email){
        return CurrentUser.from(
                userRepository.findByEmailOrThrow(email)
        );
    }
}
