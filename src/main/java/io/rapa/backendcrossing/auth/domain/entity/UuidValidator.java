package io.rapa.backendcrossing.auth.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash(value = "oauthUuid", timeToLive = 60)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UuidValidator {
    @Id
    @UuidGenerator
    private UUID id;
    
    private String userEmail;

    @Builder
    public UuidValidator(String email){
        this.userEmail = email;
    }
}
