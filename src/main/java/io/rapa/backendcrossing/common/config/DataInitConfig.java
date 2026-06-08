package io.rapa.backendcrossing.common.config;


import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.users.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DataInitConfig {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PlatformTransactionManager ptm;

    @Bean
    CommandLineRunner initUser() {
        final int BATCH_SIZE = 50;
        return args -> {
            if (userRepository.count() < 1) {
                userService.registerUser(
                        new UserCreateRequest(
                                "wjdtn747@gmail.com",
                                passwordEncoder.encode("wjdtn0619"),
                                "슈빠어드민"
                        )
                );
            }
            if(userRepository.count() < 200){
                for (int start = 0; start < 200; start += BATCH_SIZE) {

                    TransactionStatus tx =
                            ptm.getTransaction(new DefaultTransactionDefinition());
                    try {
                        for (int i = start; i < start + BATCH_SIZE && i < 200; i++) {

                            userService.registerUser(
                                    new UserCreateRequest(
                                            "사용자%d@naver.com".formatted(i),
                                            passwordEncoder.encode("wjdtn3902"),
                                            "%d정수".formatted(i)
                                    )
                            );
                        }
                        ptm.commit(tx);

                    } catch (Exception e) {
                        ptm.rollback(tx);
                        throw e;
                    }
                }
            };
        };
    }
}
