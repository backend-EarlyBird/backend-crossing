package io.rapa.backendcrossing.wallets.domain.dto;

import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import lombok.Builder;

@Builder
public record WalletDetailResponse(
        Long gold,
        Long gem
) {
    public static WalletDetailResponse from(Wallets wallets){
        return WalletDetailResponse.builder()
                .gem(wallets.getGem())
                .gold(wallets.getGold())
                .build();
    }
}
