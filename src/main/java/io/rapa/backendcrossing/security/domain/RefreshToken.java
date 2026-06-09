package io.rapa.backendcrossing.security.domain;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 604800)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Builder
public class RefreshToken {
    @Id
    private String refreshToken;
    private String email;
    public RefreshToken(String refreshToken, String email){
        this.refreshToken = refreshToken;
        this.email = email;
    }
}
