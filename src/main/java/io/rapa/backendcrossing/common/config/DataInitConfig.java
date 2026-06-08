package io.rapa.backendcrossing.common.config;


import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DataInitConfig {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    CommandLineRunner initRootUser() {
        return args -> {
            if( userRepository.count() < 2 ){
                userService.registerUser(
                        new UserCreateRequest(
                                "wjdtn747@gmail.com",
                                "wjdtn0619",
                                "슈빠어드민"
                        )
                );
            }
        };
    }
}
