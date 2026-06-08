package io.rapa.backendcrossing.inventory.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoriesService {

    private final InventoriesRepository inventoriesRepository;
    private final ItemsRepository itemsRepository;
    private final UserRepository userRepository;

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
        Users foundedUser = userRepository.findByIdOrThrow(userId);
        Items item = itemsRepository.findByIdOrThrow(itemId);
        Inventories inventory = inventoriesRepository.findBySubUserIdAndItemItemId(userId, itemId)
                .map(inv -> { inv.addQuantity(quantity); return inv; })
                .orElseGet(() -> inventoriesRepository.save(
                        Inventories.builder()
                                .subUserId(foundedUser.getUserId())
                                .user(foundedUser)
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

        Inventories inventory = inventoriesRepository.findBySubUserIdAndItemItemId(userId, itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (inventory.getQuantity() < quantity) {
            throw new CustomException(ErrorCode.ITEM_COUNT_FOUND);
        }

        if (inventory.getQuantity() == quantity) {
            // [수정] 삭제 로직 수행 후, 응답용 빈 DTO 생성
            Long userItemId = inventory.getUserItemId();
            inventoriesRepository.delete(inventory);

            // 유니티 파싱 에러를 막기 위한 최소한의 데이터가 담긴 객체
            return InventoriesResponse.builder()
                    .userItemId(userItemId)
                    .quantity(0)
                    .build();
        }

        inventory.addQuantity(-quantity);
        return InventoriesResponse.from(inventory);
    }


}