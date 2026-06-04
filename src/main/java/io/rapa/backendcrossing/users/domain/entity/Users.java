package io.rapa.backendcrossing.users.domain.entity;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.users.constants.Provider;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.constants.UserStatus;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus userStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Provider provider;

    @Column(nullable = false, length = 50)
    private String emailDomain;

    @Column(length = 255)
    private String profileImageUrl;

    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user_id")
    private List<Inventories> inventories = new ArrayList<>();

    @Builder
    public Users(
            String email,
            String password,
            String nickname
    ){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        userStatus = UserStatus.ACTIVATED;
        role = Role.USER;
        profileImageUrl = "";
        emailDomain = extractProvider(email);
        lastLoginAt = LocalDateTime.now();
        if(emailDomain.equals("gmail")) provider = Provider.GOOGLE;
        else provider = Provider.LOCAL;
    }
    private String extractProvider(String email){
        String[] s1 = email.split("@");
        String[] s2 = s1[1].split("\\.");
        return s2[0];
    }
    public Users switchToSuperAdmin(){
        role = Role.SUPER_ADMIN;
        return this;
    }
    public void setLoginTimeNow(){
        this.lastLoginAt = LocalDateTime.now();
    }
}
