package io.rapa.backendcrossing.inventory.service;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import io.rapa.backendcrossing.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
@ActiveProfiles("test")
@DisplayName("InventoriesService 단위 테스트")
public class InventoriesServiceTests {

    @Mock
    private InventoriesRepository inventoriesRepository;

    @Mock
    private ItemsRepository itemsRepository;

    @InjectMocks
    private InventoriesService inventoriesService;


    Users user = mock(Users.class);


    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("인벤토리 조회 - 성공")
    void getInventory() {
        // given
        Long userId = 1L;

        Items item = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Inventories inv = Inventories.builder().userItemId(1L).subUserId(userId)
                .user(user).item(item).quantity(3).equipped(false).build();

        given(inventoriesRepository.findByUserIdOrThrow(userId)).willReturn(Arrays.asList(inv));

        // when
        List<InventoriesResponse> result = inventoriesService.getInventory(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(3);
        verify(inventoriesRepository, times(1)).findByUserIdOrThrow(userId);
    }

    @Test
    @DisplayName("아이템 획득 - 기존 아이템 없으면 새로 저장")
    void pickupItem_newItem() {
        // given
        Long userId = 1L;
        Long itemId = 1L;
        Items item = Items.builder().itemId(itemId).itemName("연습용 검").price(100).build();

        Inventories saved = Inventories.builder().userItemId(1L).user(user).subUserId(userId).item(item).quantity(2).equipped(false).build();

        given(itemsRepository.findByIdOrThrow(itemId)).willReturn(item);
        given(inventoriesRepository.findBySubUserIdAndItemItemId(userId, itemId)).willReturn(Optional.empty());
        given(inventoriesRepository.save(any(Inventories.class))).willReturn(saved);

        given(userRepository.findByIdOrThrow(userId))
                .willReturn(user);

        // when
        inventoriesService.pickupItem(itemId, 2, userId);

        // then
        verify(inventoriesRepository, times(1)).save(any(Inventories.class));
    }

    @Test
    @DisplayName("아이템 버림 - 수량이 남으면 차감")
    void discardItem_decreaseQuantity() {
        // given
        Long userId = 1L;
        Items item = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Inventories inv = Inventories.builder().userItemId(1L).subUserId(userId).user(user).item(item).quantity(5).equipped(false).build();

        given(inventoriesRepository.findBySubUserIdAndItemItemId(userId, 1L)).willReturn(Optional.of(inv));

        // when
        inventoriesService.discardItem(1L, 2, userId);

        // then
        assertThat(inv.getQuantity()).isEqualTo(3);
        verify(inventoriesRepository, never()).delete(any());
    }

    @Test
    @DisplayName("아이템 버림 - 수량이 같으면 삭제")
    void discardItem_deleteWhenExact() {
        // given
        Long userId = 1L;
        Items item = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Inventories inv = Inventories.builder().userItemId(1L).subUserId(userId).user(user).item(item).quantity(3).equipped(false).build();

        given(inventoriesRepository.findBySubUserIdAndItemItemId(userId, 1L)).willReturn(Optional.of(inv));

        // when
        inventoriesService.discardItem(1L, 3, userId);

        // then
        verify(inventoriesRepository, times(1)).delete(inv);
    }

    @Test
    @DisplayName("아이템 버림 - 수량 초과 시 예외")
    void discardItem_throwsWhenExceedQuantity() {
        // given
        Long userId = 1L;
        Items item = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Inventories inv = Inventories.builder().userItemId(1L).user(user).subUserId(userId).item(item).quantity(1).equipped(false).build();

        given(inventoriesRepository.findBySubUserIdAndItemItemId(userId, 1L)).willReturn(Optional.of(inv));

        // when & then
        assertThrows(CustomException.class, () -> inventoriesService.discardItem(1L, 5, userId));
    }

    @Test
    @DisplayName("아이템 획득 - 기존 아이템 있으면 수량 증가")
    void pickupItem_existingItem() {
        // given
        Long userId = 1L;
        Long itemId = 1L;
        Items item = Items.builder().itemId(itemId).itemName("연습용 검").price(100).build();
        Inventories existing = Inventories.builder().user(user).userItemId(1L).subUserId(userId).item(item).quantity(3).equipped(false).build();

        given(itemsRepository.findByIdOrThrow(itemId)).willReturn(item);
        given(inventoriesRepository.findBySubUserIdAndItemItemId(userId, itemId)).willReturn(Optional.of(existing));

        // when
        inventoriesService.pickupItem(itemId, 2, userId);

        // then
        assertThat(existing.getQuantity()).isEqualTo(5);
        verify(inventoriesRepository, never()).save(any());
    }
}
