package io.rapa.backendcrossing.profiles.domain.dto;

import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import lombok.Builder;

@Builder
public record ProfileDetailResponse(
        Integer level,
        Long exp,
        Long totalPlaySeconds
) {
    public static ProfileDetailResponse from(
            Profiles profiles
    ){
        return ProfileDetailResponse.builder()
                .totalPlaySeconds(profiles.getTotalPlaySeconds())
                .level(profiles.getLevel())
                .exp(profiles.getExp())
                .build();
    }
}
