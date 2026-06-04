package io.rapa.backendcrossing.users.domain.dto.response;

import io.rapa.backendcrossing.users.domain.entity.Users;
import lombok.Builder;

@Builder
public record MeDetailResponse(
        Long userId,
        String email,
        String nickname,
        String role,
        String status,
        String provider,
        String profileImageUrl,
        String createdAt,
        String lastLoginAt
) {
    public static MeDetailResponse from(Users user){
        return MeDetailResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickName())
                .role(user.getRole().toString())
                .status(user.getUserStatus().toString())
                .provider(user.getProvider().toString())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt().toString())
                .lastLoginAt(user.getLastLoginAt().toString())
                .build();
    }
}
