package io.rapa.backendcrossing.npcs.service;

/**
 * packageName    : io.rapa.backendcrossing.npcs.service
 * fileName       : NpcShopService
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NPC 상점 서비스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.Wallets;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.repository.WalletRepository;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NpcShopService {

    private final NpcsRepository npcsRepository;
    private final NpcItemsRepository npcItemsRepository;
    private final WalletRepository walletRepository;
    private final InventoriesRepository inventoriesRepository;

    @Transactional
    public NpcPurchaseResponse purchase(Long userId, Long npcId, Long npcItemId, NpcPurchaseRequest request) {
        // NPC, 상점 아이템 유효성 검증
        npcsRepository.findByIdOrThrow(npcId);
        //NpcItems npcItem = npcItemsRepository.findByIdOrThrow(npcItemId);

        log.info("NPC ID: {}, Item ID: {}, Quantity: {}", npcId, npcItemId, request.getQuantity());

        // Fetch Join을 사용하여 NpcItems + Npc + Item 을 한 번에 로딩
        // 1+N 관련 해결용
        NpcItems npcItem = npcItemsRepository.findByIdWithDetails(npcItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND));

        /**
         * 기존 : NpcItems만 가져옴 : 연관 데이터 필요시 마다 쿼리 추가(자연로딩)
         * 현재 : Fetch Join을 사용하여 NpcItems + Npc + Item 을 한 번에 로딩(추가 쿼리를 가져오지않음)
         */

        // npcItemId가 해당 npc 소속인지 검증
        if (!npcItem.getNpc().getNpcId().equals(npcId)) {
            throw new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND);
        }

        long totalPrice = (long) npcItem.getItem().getPrice() * request.getQuantity();

        // 골드 차감
        Wallets wallet = walletRepository.findByUserIdOrThrow(userId);
        if (wallet.getGold() < totalPrice) {
            throw new CustomException(ErrorCode.INSUFFICIENT_GOLD);
        }
        wallet.deductGold(totalPrice);

        // 인벤토리에 아이템 추가 (기존 보유 시 수량 증가)
        Inventories inventory = inventoriesRepository
                .findByUserIdAndItemItemId(userId, npcItem.getItem().getItemId())
                .map(existing -> {
                    existing.addQuantity(request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> inventoriesRepository.save(
                        Inventories.builder()
                                .userId(userId)
                                .item(npcItem.getItem())
                                .quantity(request.getQuantity())
                                .build()
                ));

        return toResponse(wallet, inventory);
    }

    private NpcPurchaseResponse toResponse(Wallets wallet, Inventories inventory) {
        var item = inventory.getItem();
        return NpcPurchaseResponse.builder()
                .wallet(NpcPurchaseResponse.WalletDto.builder()
                        .gold(wallet.getGold())
                        .gem(wallet.getGem())
                        .build())
                .acquiredItem(NpcPurchaseResponse.AcquiredItemDto.builder()
                        .userItemId(inventory.getUserItemId())
                        .itemId(item.getItemId())
                        .rId(item.getRId())
                        .itemName(item.getItemName())
                        .itemType(item.getItemType())
                        .itemGrade(item.getItemGrade())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .sellPrice(item.getSellPrice())
                        .quantity(inventory.getQuantity())
                        .equipped(inventory.isEquipped())
                        .build())
                .build();
    }
}
