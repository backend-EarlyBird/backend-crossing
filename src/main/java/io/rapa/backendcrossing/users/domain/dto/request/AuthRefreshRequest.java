package io.rapa.backendcrossing.users.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthRefreshRequest(
        @NotBlank String refreshToken
) {
}
