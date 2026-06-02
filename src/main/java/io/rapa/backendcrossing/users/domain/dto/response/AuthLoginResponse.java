package io.rapa.backendcrossing.users.domain.dto.response;

import io.rapa.backendcrossing.security.domain.dto.KeyPair;
import lombok.Builder;

@Builder
public record AuthLoginResponse(
        String accessToken,
        String refreshToken,
        Integer accessExpiresInSeconds
) {
    public static AuthLoginResponse of(KeyPair keyPair, Integer sec){
        return AuthLoginResponse.builder()
                .accessToken(keyPair.accessToken())
                .refreshToken(keyPair.refreshToken())
                .accessExpiresInSeconds(sec)
                .build();
    }
}
