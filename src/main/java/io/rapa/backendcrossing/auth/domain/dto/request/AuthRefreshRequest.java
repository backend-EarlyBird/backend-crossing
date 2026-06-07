package io.rapa.backendcrossing.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthRefreshRequest(
        @NotBlank String refreshToken
) {
}
