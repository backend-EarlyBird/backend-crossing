package io.rapa.backendcrossing.security.domain.dto;

import lombok.Builder;

@Builder
public record KeyPair(
        String accessToken,
        String refreshToken
) {
}
