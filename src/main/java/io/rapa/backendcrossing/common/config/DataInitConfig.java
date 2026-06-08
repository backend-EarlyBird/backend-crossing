package io.rapa.backendcrossing.common.config;


import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    CommandLineRunner initRootUser() {
        return args -> {
            if( userRepository.count() < 2 ){
                userRepository.save(
                        Users.builder()
                                .email("wjdtn747@naver.com")
                                .password(passwordEncoder.encode("1234"))
                                .nickname("닉네임")
                                .build()
                                .switchToSuperAdmin()
                );
                userRepository.save(
                        Users.builder()
                                .email("wjdtn747@gmail.com")
                                .password(passwordEncoder.encode("1234"))
                                .nickname("닉네임")
                                .build()
                                .switchToSuperAdmin()
                );
            }
        };
    }
}
