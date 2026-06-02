package io.rapa.backendcrossing.inventory.service;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.items.entity.Items;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * packageName    : io.rapa.backendcrossing.inventory.service
 * fileName       : InventoriesServiceTests
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : InventoryService 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayName("InventoriesService 단위 테스트")
public class InventoriesServiceTests {

    @Mock
    private InventoriesRepository inventoriesRepository;

    @InjectMocks
    private InventoriesService inventoriesService;

    @Test
    @DisplayName("인벤토리 조회 - 성공")
    void getInventory() {
        // given
        Long userId = 1L;
        Items item = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Inventories inv = Inventories.builder().userItemId(1L).userId(userId).item(item).quantity(3).equipped(false).build();

        given(inventoriesRepository.findByUserId(userId)).willReturn(Arrays.asList(inv));

        // when
        List<InventoriesResponse> result = inventoriesService.getInventory(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(3);
        verify(inventoriesRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("아이템 획득 - 기존 아이템 없으면 새로 저장")
    void pickupItem_newItem() {
        // given
        Long userId = 1L;
        Items item = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();

        given(inventoriesRepository.findByUserIdAndItem(userId, item)).willReturn(Optional.empty());

        // when
        inventoriesService.pickupItem(item, 2, userId);

        // then
        verify(inventoriesRepository, times(1)).save(any(Inventories.class));
    }

    @Test
    @DisplayName("아이템 획득 - 기존 아이템 있으면 수량 증가")
    void pickupItem_existingItem() {
        // given
        Long userId = 1L;
        Items item = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Inventories existing = Inventories.builder().userItemId(1L).userId(userId).item(item).quantity(3).equipped(false).build();

        given(inventoriesRepository.findByUserIdAndItem(userId, item)).willReturn(Optional.of(existing));

        // when
        inventoriesService.pickupItem(item, 2, userId);

        // then
        assertThat(existing.getQuantity()).isEqualTo(5);
        verify(inventoriesRepository, never()).save(any());
    }
}
