package io.rapa.backendcrossing.users.domain.dto.response;

import io.rapa.backendcrossing.users.domain.entity.Users;
import lombok.Builder;

@Builder
public record UserCreateResponse(
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
    public static UserCreateResponse from(Users user){
        return UserCreateResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .status(user.getUserStatus().name())
                .provider(user.getProvider().name())
                .profileImageUrl((user.getProfileImageUrl() == null)? "null" : user.getProfileImageUrl() )
                .createdAt(user.getCreatedAt().toString())
                .lastLoginAt((user.getLastLoginAt() == null)? "null" : user.getLastLoginAt().toString() )
                .build();
    }
}
