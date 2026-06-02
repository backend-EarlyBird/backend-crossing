package io.rapa.backendcrossing.inventory.service;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
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
public class InventoriesServiceIntegrationTests {

    @Autowired
    private InventoriesService inventoriesService;

    @Autowired
    private InventoriesRepository inventoriesRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    //아이템 만들기
    private Items saveItems() {
        return itemsRepository.save(Items.builder()
                .rId("sword_001")
                .itemName("연습용 검")
                .price(100)
                .sellPrice(10)
                .itemGrade(ItemGrade.COMMON)
                .itemType(ItemType.WEAPON)
                .build());

    }


    @Test
    @DisplayName("인벤토리 조회 - 성공")
    void getInventory() {
        // given
        Long userId = 1L;
        Items item = saveItems();

        inventoriesRepository.save(Inventories.builder()
                .userId(userId).item(item).quantity(3).equipped(false).build());

        // when
        List<InventoriesResponse> result = inventoriesService.getInventory(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("아이템 획득 - 없으면 저장, 있으면 수량 증가")
    //실제 db를 쓰는거라 저장 -> 수량 증가 하나로 해야되서 하나로 함
    void pickupItem() {
        // given
        Long userId = 1L;
        Items item = saveItems();

        // when: 처음 획득 - 없으니까 새로 저장
        inventoriesService.pickupItem(item, 2, userId);

        // then
        Inventories inv = inventoriesRepository.findByUserIdAndItem(userId, item).orElseThrow();
        assertThat(inv.getQuantity()).isEqualTo(2);

        // when: 두번째 획득 - 이미 있으니까 수량 증가
        inventoriesService.pickupItem(item, 3, userId);

        // then
        assertThat(inv.getQuantity()).isEqualTo(5);
    }

}
