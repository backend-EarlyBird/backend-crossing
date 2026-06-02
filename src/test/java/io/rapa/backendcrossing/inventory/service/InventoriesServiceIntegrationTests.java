package io.rapa.backendcrossing.inventory.service;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.support.BaseIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : io.rapa.backendcrossing.inventory.service
 * fileName       : InventoriesServiceIntegrationTests
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : InventoriesService 통합테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@SpringBootTest
@Transactional
@Slf4j
@DisplayName("InventoryService 통합 테스트")
public class InventoriesServiceIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private InventoriesService inventoriesService;

    @Autowired
    private InventoriesRepository inventoriesRepository;


    @Test
    @DisplayName("인벤토리 조회 - 성공")
    void getInventory() {
        // given
        Long userId = 1L;
        inventoriesRepository.save(Inventories.builder()
                .userId(userId).item(savedItem).quantity(3).equipped(false).build());

        // when
        List<InventoriesResponse> result = inventoriesService.getInventory(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("아이템 획득 - 없으면 저장, 있으면 수량 증가")
    void pickupItem() {
        // given
        Long userId = 1L;

        // when: 처음 획득
        inventoriesService.pickupItem(savedItem.getItemId(), 2, userId);

        // then
        Inventories inv = inventoriesRepository.findByUserIdAndItemItemId(userId, savedItem.getItemId()).orElseThrow();
        assertThat(inv.getQuantity()).isEqualTo(2);

        // when: 두번째 획득
        inventoriesService.pickupItem(savedItem.getItemId(), 3, userId);

        // then
        assertThat(inv.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("아이템 버림 - 수량 차감")
    void discardItem_decreaseQuantity() {
        // given
        Long userId = 1L;
        Inventories inv = inventoriesRepository.save(
                Inventories.builder().userId(userId).item(savedItem).quantity(5).equipped(false).build());

        // when
        inventoriesService.discardItem(inv.getUserItemId(), 2, userId);

        // then
        assertThat(inv.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("아이템 버림 - 수량 일치 시 삭제")
    void discardItem_deleteWhenExact() {
        // given
        Long userId = 1L;
        Inventories inv = inventoriesRepository.save(
                Inventories.builder().userId(userId).item(savedItem).quantity(3).equipped(false).build());
        Long userItemId = inv.getUserItemId();

        // when
        inventoriesService.discardItem(userItemId, 3, userId);

        // then
        assertThat(inventoriesRepository.findById(userItemId)).isEmpty();
    }

}
