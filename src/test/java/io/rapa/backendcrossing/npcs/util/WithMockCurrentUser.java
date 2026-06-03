package io.rapa.backendcrossing.npcs.util;

import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.constants.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * packageName    : io.rapa.backendcrossing.npcs.util
 * fileName       : WithMockCurrentUser
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : 테스트용 CurrentUser SecurityContext 주입 어노테이션
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCurrentUser.Factory.class)
public @interface WithMockCurrentUser {

    long userId() default 1L;
    String email() default "test@test.com";
    String nickName() default "테스터";

    class Factory implements WithSecurityContextFactory<WithMockCurrentUser> {
        @Override
        public SecurityContext createSecurityContext(WithMockCurrentUser annotation) {
            CurrentUser currentUser = CurrentUser.builder()
                    .email(annotation.email())
                    .nickName(annotation.nickName())
                    .build()
                    .setId(annotation.userId())
                    .setRole(Role.USER);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            return context;
        }
    }
}
