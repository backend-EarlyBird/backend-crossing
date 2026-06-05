package io.rapa.backendcrossing.profiles.domain.entity;

import io.rapa.backendcrossing.users.domain.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profiles {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Long exp;

    @Column(nullable = false)
    private Long totalPlaySeconds;

    @Builder
    public Profiles(
            Integer level,
            Long exp,
            Long totalPlaySeconds
    ){
        this.exp = exp;
        this.level = level;
        this.totalPlaySeconds = totalPlaySeconds;
    }
}
