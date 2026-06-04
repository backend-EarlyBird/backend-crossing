package io.rapa.backendcrossing.npcs.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.npcs.repository.WalletRepository;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * packageName    : io.rapa.backendcrossing.npcs.service
 * fileName       : NpcShopServiceIntegrationTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcShopService 통합 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@SpringBootTest
@Transactional
@Slf4j
@DisplayName("NpcShopService 통합 테스트")
public class NpcShopServiceIntegrationTests {

    @Autowired private NpcShopService npcShopService;
    @Autowired private NpcsRepository npcsRepository;
    @Autowired private NpcItemsRepository npcItemsRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private InventoriesRepository inventoriesRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ItemsRepository itemsRepository;

    private Long userId;
    private Long npcId;
    private Long npcItemId;
    private NpcPurchaseRequest request;

    @BeforeEach
    void setUp() throws Exception {
        Users user = userRepository.save(
                Users.builder().email("test@test.com").password("password123").nickName("테스터").build()
        );
        userId = user.getUserId();

        walletRepository.save(Wallets.builder().user(user).gold(5000L).gem(10L).build());

        Items item = itemsRepository.save(Items.builder()
                .rId("potion_hp_001").itemName("HP 포션")
                .itemType(ItemType.CONSUMABLE).itemGrade(ItemGrade.COMMON)
                .description("HP를 50 회복합니다.").price(30).sellPrice(10)
                .build());

        Npcs npc = npcsRepository.save(
                Npcs.builder().rId("npc_merchant").name("상인").locationKey("market").active(true).build()
        );
        npcId = npc.getNpcId();

        NpcItems npcItem = npcItemsRepository.save(
                NpcItems.builder().npc(npc).item(item).quantity(99).sortOrder(1).build()
        );
        npcItemId = npcItem.getNpcItemId();

        request = new NpcPurchaseRequest();
        var field = NpcPurchaseRequest.class.getDeclaredField("quantity");
        field.setAccessible(true);
        field.set(request, 3);
    }

    @Test
    @DisplayName("아이템 구매 - 성공 (신규 아이템)")
    void purchase_Success_NewItem() {
        // when
        NpcPurchaseResponse result = npcShopService.purchase(userId, npcId, npcItemId, request);

        // then
        assertThat(result.getWallet().getGold()).isEqualTo(5000L - 90L); // 30 * 3
        assertThat(result.getAcquiredItem().getQuantity()).isEqualTo(3);
        assertThat(result.getAcquiredItem().getRId()).isEqualTo("potion_hp_001");

        Optional<Inventories> saved = inventoriesRepository.findByUserIdAndItemItemId(userId, result.getAcquiredItem().getItemId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("아이템 구매 - 성공 (기존 보유 아이템 수량 증가)")
    void purchase_Success_ExistingItem() {
        // given: 먼저 1번 구매
        npcShopService.purchase(userId, npcId, npcItemId, request);

        // when: 동일 아이템 한 번 더 구매
        NpcPurchaseResponse result = npcShopService.purchase(userId, npcId, npcItemId, request);

        // then
        assertThat(result.getAcquiredItem().getQuantity()).isEqualTo(6); // 3 + 3

        Optional<Inventories> saved = inventoriesRepository.findByUserIdAndItemItemId(userId, result.getAcquiredItem().getItemId());
        assertThat(saved.get().getQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("아이템 구매 - 실패 (골드 부족)")
    void purchase_Fail_InsufficientGold() throws Exception {
        // given: 골드를 10으로 줄임 (30*3=90 필요)
        Wallets wallet = walletRepository.findByUserIdOrThrow(userId);
        var goldField = Wallets.class.getDeclaredField("gold");
        goldField.setAccessible(true);
        goldField.set(wallet, 10L);

        // when / then
        CustomException exception = assertThrows(CustomException.class, () ->
                npcShopService.purchase(userId, npcId, npcItemId, request)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INSUFFICIENT_GOLD.getDescription());
    }

    @Test
    @DisplayName("아이템 구매 - 실패 (NPC 없음)")
    void purchase_Fail_NpcNotFound() {
        CustomException exception = assertThrows(CustomException.class, () ->
                npcShopService.purchase(userId, 999999L, npcItemId, request)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NPC_NOT_FOUND.getDescription());
    }

    @Test
    @DisplayName("아이템 구매 - 실패 (상점 아이템 없음)")
    void purchase_Fail_ShopItemNotFound() {
        CustomException exception = assertThrows(CustomException.class, () ->
                npcShopService.purchase(userId, npcId, 999999L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND.getDescription());
    }
}
