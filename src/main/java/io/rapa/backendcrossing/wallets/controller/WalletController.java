package io.rapa.backendcrossing.wallets.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.wallets.domain.dto.WalletDetailResponse;
import io.rapa.backendcrossing.wallets.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/v1/users/me/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<CommonResponse<WalletDetailResponse>> getWalletDetail(
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        return ResponseEntity.ok(
                        CommonResponse.successWithMessage(
                                walletService.getWalletDetails(currentUser.getId()),
                                null
                        )
                );
    }
}
