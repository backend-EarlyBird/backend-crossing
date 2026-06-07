package io.rapa.backendcrossing.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthGoogleExchangeRequest(
    @NotBlank String code,
    @NotBlank String state
) {
}
