package io.rapa.backendcrossing.npcs.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.npcs.entity.UserItem;
import io.rapa.backendcrossing.npcs.entity.Wallet;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.repository.UserItemRepository;
import io.rapa.backendcrossing.npcs.repository.WalletRepository;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * packageName    : io.rapa.backendcrossing.npcs.service
 * fileName       : NpcShopServiceTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcShopService 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayName("NpcShopService 단위 테스트")
public class NpcShopServiceTests {

    @Mock private NpcsRepository npcsRepository;
    @Mock private NpcItemsRepository npcItemsRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private UserItemRepository userItemRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private NpcShopService npcShopService;

    private Npcs npc;
    private Items item;
    private NpcItems npcItem;
    private Wallet wallet;
    private Users user;
    private NpcPurchaseRequest request;

    @BeforeEach
    void setUp() {
        npc = Npcs.builder().npcId(1L).rId("npc_merchant").name("상인").locationKey("market").active(true).build();

        item = Items.builder()
                .itemId(2L).rId("potion_hp_001").itemName("HP 포션")
                .itemType(ItemType.CONSUMABLE).itemGrade(ItemGrade.COMMON)
                .description("HP를 50 회복합니다.").price(30).sellPrice(10)
                .build();

        npcItem = NpcItems.builder().npcItemId(1L).npc(npc).item(item).quantity(99).sortOrder(1).build();

        wallet = Wallet.builder().walletId(1L).gold(5000L).gem(10L).build();

        request = new NpcPurchaseRequest();
        // reflection으로 quantity 세팅 (private 필드)
        try {
            var field = NpcPurchaseRequest.class.getDeclaredField("quantity");
            field.setAccessible(true);
            field.set(request, 3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("아이템 구매 - 성공 (신규 아이템)")
    void purchase_Success_NewItem() {
        // given
        Long userId = 1L;
        given(npcsRepository.findByIdOrThrow(1L)).willReturn(npc);
        given(npcItemsRepository.findByIdOrThrow(1L)).willReturn(npcItem);
        given(walletRepository.findByUserIdOrThrow(userId)).willReturn(wallet);
        given(userItemRepository.findByUserUserIdAndItemItemId(userId, item.getItemId())).willReturn(Optional.empty());
        given(userRepository.findByIdOrThrow(userId)).willReturn(user);

        UserItem savedUserItem = UserItem.builder()
                .userItemId(1L).item(item).quantity(3).acquiredAt(LocalDateTime.now()).build();
        given(userItemRepository.save(any())).willReturn(savedUserItem);

        // when
        NpcPurchaseResponse result = npcShopService.purchase(userId, 1L, 1L, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getWallet().getGold()).isEqualTo(5000L - 90L); // 30 * 3
        assertThat(result.getAcquiredItem().getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("아이템 구매 - 성공 (기존 보유 아이템 수량 증가)")
    void purchase_Success_ExistingItem() {
        // given
        Long userId = 1L;
        UserItem existingItem = UserItem.builder()
                .userItemId(1L).item(item).quantity(5).acquiredAt(LocalDateTime.now()).build();

        given(npcsRepository.findByIdOrThrow(1L)).willReturn(npc);
        given(npcItemsRepository.findByIdOrThrow(1L)).willReturn(npcItem);
        given(walletRepository.findByUserIdOrThrow(userId)).willReturn(wallet);
        given(userItemRepository.findByUserUserIdAndItemItemId(userId, item.getItemId())).willReturn(Optional.of(existingItem));

        // when
        NpcPurchaseResponse result = npcShopService.purchase(userId, 1L, 1L, request);

        // then
        assertThat(result.getAcquiredItem().getQuantity()).isEqualTo(8); // 5 + 3
        verify(userItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("아이템 구매 - 실패 (골드 부족)")
    void purchase_Fail_InsufficientGold() {
        // given
        Long userId = 1L;
        Wallet poorWallet = Wallet.builder().walletId(1L).gold(10L).gem(0L).build(); // 30*3=90 필요

        given(npcsRepository.findByIdOrThrow(1L)).willReturn(npc);
        given(npcItemsRepository.findByIdOrThrow(1L)).willReturn(npcItem);
        given(walletRepository.findByUserIdOrThrow(userId)).willReturn(poorWallet);

        // when / then
        CustomException exception = assertThrows(CustomException.class, () ->
                npcShopService.purchase(userId, 1L, 1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INSUFFICIENT_GOLD.getDescription());
    }

    @Test
    @DisplayName("아이템 구매 - 실패 (NPC 없음)")
    void purchase_Fail_NpcNotFound() {
        // given
        Long userId = 1L;
        given(npcsRepository.findByIdOrThrow(999L)).willThrow(new CustomException(ErrorCode.NPC_NOT_FOUND));

        // when / then
        CustomException exception = assertThrows(CustomException.class, () ->
                npcShopService.purchase(userId, 999L, 1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NPC_NOT_FOUND.getDescription());
    }

    @Test
    @DisplayName("아이템 구매 - 실패 (상점 아이템 없음)")
    void purchase_Fail_ShopItemNotFound() {
        // given
        Long userId = 1L;
        given(npcsRepository.findByIdOrThrow(1L)).willReturn(npc);
        given(npcItemsRepository.findByIdOrThrow(999L)).willThrow(new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND));

        // when / then
        CustomException exception = assertThrows(CustomException.class, () ->
                npcShopService.purchase(userId, 1L, 999L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND.getDescription());
    }
}
