package io.rapa.backendcrossing.profiles.service;

import io.rapa.backendcrossing.profiles.domain.dto.ProfileDetailResponse;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepository;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableMethodSecurity(prePostEnabled = true)
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final UserBoundaryRepository userBoundaryRepository;

    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
    public ProfileDetailResponse getDetail(Long userId){
        return ProfileDetailResponse.from(
                userBoundaryRepository.findProfileByUserIdOrThrow(userId)
        );
    }
}
