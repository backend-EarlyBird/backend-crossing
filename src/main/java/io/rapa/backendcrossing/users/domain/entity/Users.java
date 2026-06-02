package io.rapa.backendcrossing.users.domain.entity;

import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.constants.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true , nullable = false,length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus userStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(length = 255)
    private String profileImageUrl;

    private Instant lastLoginAt;

    @Builder
    public Users(
            String email,
            String password,
            String nickName
    ){
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        userStatus = UserStatus.ACTIVE;
        role = Role.USER;
        provider = extractProvider(email);
    }
    public String extractProvider(String email){
        String[] s1 = email.split("@");
        String[] s2 = s1[1].split("\\.");
        return s2[0];
    }
    public Users switchToSuperAdmin(){
        role = Role.SUPER_ADMIN;
        return this;
    }
}
