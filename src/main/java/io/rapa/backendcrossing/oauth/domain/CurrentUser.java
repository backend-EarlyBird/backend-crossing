package io.rapa.backendcrossing.oauth.domain;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.domain.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Accessors(chain = true)
public class CurrentUser implements OAuth2User {
    @Setter
    private Long id;
    private String email;
    private String name;
    private String nickName;
    @Setter
    private Role role;
    private Map<String, Object> attributes;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(this.role.name().toUpperCase())
        );
    }
    @Builder
    private CurrentUser(
            String email,
            String nickName
    ){
        this.email = email;
        this.nickName = nickName;
        this.name = nickName;
    }
    public static CurrentUser from(Users user){
        if(user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND, "계정이 없어 UserDetails의 생성이 불가능합니다.");
        return CurrentUser.builder()
                .nickName(user.getNickName())
                .email(user.getEmail())
                .build()
                .setId(user.getUserId())
                .setRole(user.getRole());
    }
}
