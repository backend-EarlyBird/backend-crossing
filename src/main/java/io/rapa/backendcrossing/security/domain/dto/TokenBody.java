package io.rapa.backendcrossing.security.domain.dto;

import io.rapa.backendcrossing.users.constants.Role;
import lombok.Builder;

@Builder
public record TokenBody(
        String email,
        Role role
) {
}
