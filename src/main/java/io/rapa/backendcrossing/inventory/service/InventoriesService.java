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
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /*
    * 유저가 아이템 획득 시 인벤토리 변화
    * */
    @Transactional
    public void pickupItem(Long itemId, int quantity, Long userId){
        try {
            Items item = itemsRepository.findByIdOrThrow(itemId);
            inventoriesRepository.findByUserIdAndItemItemId(userId, itemId)
                    .ifPresentOrElse(
                            inv -> inv.addQuantity(quantity),
                            () -> inventoriesRepository.save(
                                    Inventories.builder()
                                            .userId(userId)
                                            .item(item)
                                            .quantity(quantity)
                                            .equipped(false)
                                            .build()
                            )
                    );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            //404에러
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }


    private InventoriesResponse convertToDto(Inventories inventory) {
        return InventoriesResponse.builder()
                .userItemId(inventory.getUserItemId())
                .item(inventory.getItem())
                .quantity(inventory.getQuantity())
                .equipped(inventory.isEquipped())
                .acquiredAt(inventory.getAcquiredAt())
                .build();
    }
}