package io.rapa.backendcrossing.inventory.service;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.items.entity.Items;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

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

    /*
    * 유저 인벤토리 조회
    * */
    public List<InventoriesResponse> getInventory(Long userId){
        //유저 인벤토리 조회
        return inventoriesRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /*
    * 유저가 아이템 획득 시 인벤토리 변화
    * */
    @Transactional
    public void pickupItem(Items item, int quantity, Long userId){

        //아이템이 있으면 수량만 증가하고 없으면 새로 insert하기
        inventoriesRepository.findByUserIdAndItem(userId, item)
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