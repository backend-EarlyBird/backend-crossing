package io.rapa.backendcrossing.security.dto;

import lombok.Builder;

@Builder
public record KeyPair(
        String accessToken,
        String refreshToken
) {
}
