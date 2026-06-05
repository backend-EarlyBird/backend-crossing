package io.rapa.backendcrossing.wallets.service;

import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.repository.UserBoundaryRepository;
import io.rapa.backendcrossing.wallets.domain.dto.WalletDetailResponse;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;


@Service
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WalletService {

    private final UserBoundaryRepository userBoundaryRepository;

    private final TokenService tokenService;

    @PreAuthorize("#userId == authentication.principal.id and isAuthenticated()")
    public WalletDetailResponse getWalletDetails(Long userId){

        Wallets foundedWallet = userBoundaryRepository.findWalletByUserIdOrThrow(userId);

        return WalletDetailResponse.from(foundedWallet);
    }

}
