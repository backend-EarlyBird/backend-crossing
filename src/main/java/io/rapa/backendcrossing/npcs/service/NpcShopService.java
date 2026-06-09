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

import io.rapa.backendcrossing.common.aop.annotation.CustomLock;
import io.rapa.backendcrossing.common.aop.annotation.SpecLogger;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NpcShopService {

    private final NpcsRepository npcsRepository;
    private final NpcItemsRepository npcItemsRepository;
    private final WalletRepository walletRepository;
    private final InventoriesRepository inventoriesRepository;

    private final EntityManager entityManager;

    // 락이 적용되지 않은 메서드
    @SpecLogger
    @Transactional
    public NpcPurchaseResponse purchase(Long userId, Long npcId, Long npcItemId, NpcPurchaseRequest request) {
        // 유효성 검증
        npcsRepository.findByIdOrThrow(npcId);

        // Fetch Join된 아이템 로딩
        NpcItems npcItem = npcItemsRepository.findByIdWithDetails(npcItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND));

        // [소속 검증] - 확실하게 여기서 체크합니다.
        if (!npcItem.getNpc().getNpcId().equals(npcId)) {
            throw new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND);
        }

        // NPC 상점 아이템 재고 확인
        if (npcItem.getQuantity() < request.getQuantity()) {
            throw new CustomException(ErrorCode.INVALID_INPUT); // 재고 부족 예외
        }

        //NPC 상점 재고 차감
        int updatedStock = npcItem.getQuantity() - request.getQuantity();
        npcItem.setQuantity(updatedStock);

        // 재화 확인 및 차감
        long totalPrice = (long) npcItem.getItem().getPrice() * request.getQuantity();
        Wallets wallet = walletRepository.findByUserIdOrThrow(userId);
        if (wallet.getGold() < totalPrice) {
            throw new CustomException(ErrorCode.INSUFFICIENT_GOLD);
        }
        wallet.deductGold(totalPrice); // JPA가 Dirty Checking으로 자동 update

        Long itemId = npcItem.getItem().getItemId();

        // UPSERT 실행 (DB에 반영됨)
        log.info("UPSERT 파라미터 확인: subUserId={}, itemId={}, userId={}, quantity={}",
                userId, npcItem.getItem().getItemId(), userId, request.getQuantity());
        inventoriesRepository.upsertQuantity(userId, itemId, userId, request.getQuantity());


        return NpcPurchaseResponse.builder()
                .wallet(NpcPurchaseResponse.WalletDto.builder()
                        .gold(wallet.getGold())
                        .gem(wallet.getGem())
                        .build())
                .acquiredItem(NpcPurchaseResponse.AcquiredItemDto.builder()
                        .itemId(npcItem.getItem().getItemId())
                        .rId(npcItem.getItem().getRId())
                        .itemName(npcItem.getItem().getItemName())
                        .itemType(npcItem.getItem().getItemType())
                        .itemGrade(npcItem.getItem().getItemGrade())
                        .description(npcItem.getItem().getDescription())
                        .price(npcItem.getItem().getPrice())
                        .sellPrice(npcItem.getItem().getSellPrice())
                        .quantity(request.getQuantity()) // 요청한 수량 그대로 사용
                        .equipped(false) // 신규 획득/수량 추가 시 기본값
                        .acquiredAt(LocalDateTime.now()) // 현재 시각
                        .build())
                .build();
    }

//    // 락이 적용된 메서드
//    @CustomLock(key = CustomLock.Key.STOCK)
//    public NpcPurchaseResponse purchaseWithLock(Long userId, Long npcId, Long npcItemId, NpcPurchaseRequest request) {
//        // 유효성 검증
//        npcsRepository.findByIdOrThrow(npcId);
//
//        // Fetch Join된 아이템 로딩
//        NpcItems npcItem = npcItemsRepository.findByIdWithDetails(npcItemId)
//                .orElseThrow(() -> new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND));
//
//        // [소속 검증] - 확실하게 여기서 체크합니다.
//        if (!npcItem.getNpc().getNpcId().equals(npcId)) {
//            throw new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND);
//        }
//
//        // NPC 상점 아이템 재고 확인
//        if (npcItem.getQuantity() < request.getQuantity()) {
//            throw new CustomException(ErrorCode.INVALID_INPUT); // 재고 부족 예외
//        }
//
//        //NPC 상점 재고 차감
//        int updatedStock = npcItem.getQuantity() - request.getQuantity();
//        npcItem.setQuantity(updatedStock);
//
//        // 재화 확인 및 차감
//        long totalPrice = (long) npcItem.getItem().getPrice() * request.getQuantity();
//        Wallets wallet = walletRepository.findByUserIdOrThrow(userId);
//        if (wallet.getGold() < totalPrice) {
//            throw new CustomException(ErrorCode.INSUFFICIENT_GOLD);
//        }
//        wallet.deductGold(totalPrice); // JPA가 Dirty Checking으로 자동 update
//
//        Long itemId = npcItem.getItem().getItemId();
//
//        // UPSERT 실행 (DB에 반영됨)
//        log.info("UPSERT 파라미터 확인: subUserId={}, itemId={}, userId={}, quantity={}",
//                userId, npcItem.getItem().getItemId(), userId, request.getQuantity());
//        inventoriesRepository.upsertQuantity(userId, itemId, userId, request.getQuantity());
//
//
//        return NpcPurchaseResponse.builder()
//                .wallet(NpcPurchaseResponse.WalletDto.builder()
//                        .gold(wallet.getGold())
//                        .gem(wallet.getGem())
//                        .build())
//                .acquiredItem(NpcPurchaseResponse.AcquiredItemDto.builder()
//                        .itemId(npcItem.getItem().getItemId())
//                        .rId(npcItem.getItem().getRId())
//                        .itemName(npcItem.getItem().getItemName())
//                        .itemType(npcItem.getItem().getItemType())
//                        .itemGrade(npcItem.getItem().getItemGrade())
//                        .description(npcItem.getItem().getDescription())
//                        .price(npcItem.getItem().getPrice())
//                        .sellPrice(npcItem.getItem().getSellPrice())
//                        .quantity(request.getQuantity()) // 요청한 수량 그대로 사용
//                        .equipped(false) // 신규 획득/수량 추가 시 기본값
//                        .acquiredAt(LocalDateTime.now()) // 현재 시각
//                        .build())
//                .build();
//    }

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
