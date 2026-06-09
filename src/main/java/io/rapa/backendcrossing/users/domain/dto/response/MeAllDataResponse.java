package io.rapa.backendcrossing.users.domain.dto.response;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.profiles.domain.dto.ProfileDetailResponse;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.wallets.domain.dto.WalletDetailResponse;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import lombok.Builder;

import java.util.List;

@Builder
public record MeAllDataResponse(
      MeDetailResponse account,
      ProfileDetailResponse profile,
      WalletDetailResponse wallet,
      List<InventoriesResponse> inventory,
      Integer friendCount
) {

    public static MeAllDataResponse from(
            Users user,
            Profiles profile,
            Wallets wallets,
            List<Inventories> inventories,
            Integer friendCount
    ){
        return MeAllDataResponse.builder()
                .account(MeDetailResponse.from(user))
                .profile(ProfileDetailResponse.from(profile))
                .wallet(WalletDetailResponse.from(wallets))
                .inventory(
                        inventories.stream()
                                .map(inventory -> InventoriesResponse.from(inventory))
                                .toList()
                )
                .friendCount(friendCount)
                .build();
    }

}
