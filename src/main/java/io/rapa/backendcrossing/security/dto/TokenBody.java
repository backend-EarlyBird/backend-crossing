package io.rapa.backendcrossing.security.dto;

import io.rapa.backendcrossing.users.constants.Role;
import lombok.Builder;

@Builder
public record TokenBody(
        String email,
        Role role
) {
}
