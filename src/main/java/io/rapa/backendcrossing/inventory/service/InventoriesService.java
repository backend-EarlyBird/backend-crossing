package io.rapa.backendcrossing.inventory.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : io.rapa.backendcrossing.inventory.service
 * fileName       : InventoriesService
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : Inventories 서비스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@Service
@RequiredArgsConstructor
public class InventoriesService {

    private final InventoriesRepository inventoriesRepository;
    private final ItemsRepository itemsRepository;

    /*
    * 유저 인벤토리 조회
    * */
    public List<InventoriesResponse> getInventory(Long userId){
        return inventoriesRepository.findByUserIdOrThrow(userId).stream()
                .map(InventoriesResponse::from)
                .collect(Collectors.toList());
    }

    /*
    * 유저가 아이템 획득 시 인벤토리 변화
    * */
    @Transactional
    public InventoriesResponse pickupItem(Long itemId, int quantity, Long userId) {
        Items item = itemsRepository.findByIdOrThrow(itemId);
        Inventories inventory = inventoriesRepository.findByUserIdAndItemItemId(userId, itemId)
                .map(inv -> { inv.addQuantity(quantity); return inv; })
                .orElseGet(() -> inventoriesRepository.save(
                        Inventories.builder()
                                .userId(userId)
                                .item(item)
                                .quantity(quantity)
                                .equipped(false)
                                .build()
                ));
        return InventoriesResponse.from(inventory);
    }

    /*
    * 사용자가 아이템을 버림
    * */
    @Transactional
    public InventoriesResponse discardItem(Long itemId, int quantity, Long userId) {
        Inventories inventory = inventoriesRepository.findByUserIdAndItemItemId(userId, itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        if (inventory.getQuantity() < quantity) throw new CustomException(ErrorCode.ITEM_COUNT_FOUND);
        if (inventory.getQuantity() == quantity) inventoriesRepository.delete(inventory);
        else inventory.addQuantity(-quantity);

        return InventoriesResponse.from(inventory);
    }
}