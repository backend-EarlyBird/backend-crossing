package io.rapa.backendcrossing.users.domain.dto.response;

import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.profiles.domain.dto.ProfileDetailResponse;
import io.rapa.backendcrossing.wallets.domain.dto.WalletDetailResponse;

import java.util.List;

public record MeAllDataResponse(
      MeDetailResponse account,
      ProfileDetailResponse profile,
      WalletDetailResponse wallet,
      List<InventoriesResponse> inventory,
      Integer friendCount
) {
}
