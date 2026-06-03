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
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.UserItem;
import io.rapa.backendcrossing.npcs.entity.Wallet;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.repository.UserItemRepository;
import io.rapa.backendcrossing.npcs.repository.WalletRepository;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NpcShopService {

    private final NpcsRepository npcsRepository;
    private final NpcItemsRepository npcItemsRepository;
    private final WalletRepository walletRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public NpcPurchaseResponse purchase(Long userId, Long npcId, Long npcItemId, NpcPurchaseRequest request) {
        // NPC, 상점 아이템 유효성 검증
        npcsRepository.findByIdOrThrow(npcId);
        NpcItems npcItem = npcItemsRepository.findByIdOrThrow(npcItemId);

        // npcItemId가 해당 npc 소속인지 검증
        if (!npcItem.getNpc().getNpcId().equals(npcId)) {
            throw new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND);
        }

        long totalPrice = (long) npcItem.getItem().getPrice() * request.getQuantity();

        // 골드 차감
        Wallet wallet = walletRepository.findByUserIdOrThrow(userId);
        if (wallet.getGold() < totalPrice) {
            throw new CustomException(ErrorCode.INSUFFICIENT_GOLD);
        }
        wallet.deductGold(totalPrice);

        // 인벤토리에 아이템 추가 (기존 보유 시 수량 증가)
        UserItem userItem = userItemRepository
                .findByUserUserIdAndItemItemId(userId, npcItem.getItem().getItemId())
                .map(existing -> {
                    existing.addQuantity(request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> userItemRepository.save(
                        UserItem.builder()
                                .user(userRepository.findByIdOrThrow(userId))
                                .item(npcItem.getItem())
                                .quantity(request.getQuantity())
                                .acquiredAt(LocalDateTime.now())
                                .build()
                ));

        return toResponse(wallet, userItem);
    }

    private NpcPurchaseResponse toResponse(Wallet wallet, UserItem userItem) {
        var item = userItem.getItem();
        return NpcPurchaseResponse.builder()
                .wallet(NpcPurchaseResponse.WalletDto.builder()
                        .gold(wallet.getGold())
                        .gem(wallet.getGem())
                        .build())
                .acquiredItem(NpcPurchaseResponse.AcquiredItemDto.builder()
                        .userItemId(userItem.getUserItemId())
                        .itemId(item.getItemId())
                        .rId(item.getRId())
                        .itemName(item.getItemName())
                        .itemType(item.getItemType())
                        .itemGrade(item.getItemGrade())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .sellPrice(item.getSellPrice())
                        .quantity(userItem.getQuantity())
                        .equipped(userItem.isEquipped())
                        .acquiredAt(userItem.getAcquiredAt())
                        .build())
                .build();
    }
}
