package io.rapa.backendcrossing.profiles.domain.dto;

import lombok.Builder;

@Builder
public record ProfileDetailResponse(
        Integer level,
        Long exp,
        Long totalPlaySeconds
) {
    public static ProfileDetailResponse from(
            Integer level,
            Long exp,
            Long totalPlaySeconds
    ){
        return ProfileDetailResponse.builder()
                .totalPlaySeconds(totalPlaySeconds)
                .level(level)
                .exp(exp)
                .build();
    }
}
