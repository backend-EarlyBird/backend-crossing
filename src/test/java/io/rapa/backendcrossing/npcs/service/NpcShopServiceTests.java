package io.rapa.backendcrossing.npcs.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
@ActiveProfiles("test")
@DisplayName("NpcShopService 단위 테스트")
public class NpcShopServiceTests {

    @Mock private NpcsRepository npcsRepository;
    @Mock private NpcItemsRepository npcItemsRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private InventoriesRepository inventoriesRepository;

    @InjectMocks
    private NpcShopService npcShopService;

    private Npcs npc;
    private Items item;
    private NpcItems npcItem;
    private Wallets wallet;
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

        wallet = new Wallets(1L, 10L, 5000L);

        request = new NpcPurchaseRequest();
        try {
            var field = NpcPurchaseRequest.class.getDeclaredField("quantity");
            field.setAccessible(true);
            field.set(request, 3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("아이템 구매 - 성공")
    void purchase_Success() {
        // given
        Long userId = 1L;
        given(npcsRepository.findByIdOrThrow(1L)).willReturn(npc);
        given(npcItemsRepository.findByIdWithDetails(1L)).willReturn(Optional.of(npcItem));
        given(walletRepository.findByUserIdOrThrow(userId)).willReturn(wallet);

        // when
        NpcPurchaseResponse result = npcShopService.purchase(userId, 1L, 1L, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getWallet().getGold()).isEqualTo(5000L - 90L); // 30 * 3
        assertThat(result.getAcquiredItem().getQuantity()).isEqualTo(3);
        verify(inventoriesRepository).upsertQuantity(userId, item.getItemId(), userId, 3);
    }

    @Test
    @DisplayName("아이템 구매 - 실패 (골드 부족)")
    void purchase_Fail_InsufficientGold() {
        // given
        Long userId = 1L;
        Wallets poorWallet = new Wallets(1L, 0L, 10L); // 30*3=90 필요

        given(npcsRepository.findByIdOrThrow(1L)).willReturn(npc);
        given(npcItemsRepository.findByIdWithDetails(1L)).willReturn(Optional.of(npcItem));
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
        given(npcItemsRepository.findByIdWithDetails(999L)).willReturn(Optional.empty());

        // when / then
        assertThrows(CustomException.class, () ->
                npcShopService.purchase(userId, 1L, 999L, request)
        );
    }
}
