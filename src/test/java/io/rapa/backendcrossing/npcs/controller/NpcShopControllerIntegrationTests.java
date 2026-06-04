package io.rapa.backendcrossing.npcs.controller;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.npcs.entity.Wallets;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.repository.WalletRepository;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.npcs.controller
 * fileName       : NpcShopControllerIntegrationTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcShopController 통합 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
@DisplayName("Describe: NPC 상점 아이템 구매 ( POST /api/v1/users/me/npcs/{npcId}/items/{npcItemId}/purchase )")
public class NpcShopControllerIntegrationTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired WalletRepository walletRepository;
    @Autowired NpcsRepository npcsRepository;
    @Autowired NpcItemsRepository npcItemsRepository;
    @Autowired ItemsRepository itemsRepository;
    @Autowired PasswordEncoder passwordEncoder;

    final String BASE_ENDPOINT = "/api/v1/users/me/npcs";

    Users testUser;
    Npcs testNpc;
    NpcItems testNpcItem;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(
                UserUtils.makeUsers("npc_shop_test@naver.com", passwordEncoder.encode("1234"))
        );

        walletRepository.save(Wallets.builder().user(testUser).gold(5000L).gem(10L).build());

        Items item = itemsRepository.save(Items.builder()
                .rId("potion_hp_001").itemName("HP 포션")
                .itemType(ItemType.CONSUMABLE).itemGrade(ItemGrade.COMMON)
                .description("HP를 50 회복합니다.").price(30).sellPrice(10)
                .build());

        testNpc = npcsRepository.save(
                Npcs.builder().rId("npc_merchant").name("상인").locationKey("market").active(true).build()
        );

        testNpcItem = npcItemsRepository.save(
                NpcItems.builder().npc(testNpc).item(item).quantity(99).sortOrder(1).build()
        );
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor auth() {
        CurrentUser currentUser = CurrentUser.builder()
                .email(testUser.getEmail()).nickName(testUser.getNickName()).build()
                .setId(testUser.getUserId())
                .setRole(testUser.getRole());
        return SecurityMockMvcRequestPostProcessors.authentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities())
        );
    }

    private ResultActions purchase(int quantity) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("quantity", quantity));
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .post(BASE_ENDPOINT + "/" + testNpc.getNpcId() + "/items/" + testNpcItem.getNpcItemId() + "/purchase")
                        .with(auth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );
    }

    @Nested
    @DisplayName("Context: 올바른 요청이 주어진 경우")
    class Context_with_valid_request {

        @Test
        @DisplayName("It: 200 OK와 함께 구매 결과 반환")
        void It_구매_성공() throws Exception {
            purchase(3)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value(SuccessMessage.NPC_PURCHASE_SUCCESS.getMessage()))
                    .andExpect(jsonPath("$.data.wallet.gold").value(5000 - 90))
                    .andExpect(jsonPath("$.data.acquiredItem.quantity").value(3))
                    .andExpect(jsonPath("$.data.acquiredItem.rId").value("potion_hp_001"));
        }
    }

    @Nested
    @DisplayName("Context: 골드가 부족한 경우")
    class Context_insufficient_gold {

        @Test
        @DisplayName("It: 400 BAD REQUEST 반환")
        void It_골드부족_실패() throws Exception {
            Wallets wallet = walletRepository.findByUserIdOrThrow(testUser.getUserId());
            wallet.deductGold(4990L); // 5000 -> 10
            walletRepository.flush();

            purchase(3)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(ErrorCode.INSUFFICIENT_GOLD.getDescription()));
        }
    }

    @Nested
    @DisplayName("Context: 존재하지 않는 NPC인 경우")
    class Context_npc_not_found {

        @Test
        @DisplayName("It: 404 NOT FOUND 반환")
        void It_NPC없음_실패() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("quantity", 1));
            mockMvc.perform(
                    MockMvcRequestBuilders
                            .post(BASE_ENDPOINT + "/999999/items/" + testNpcItem.getNpcItemId() + "/purchase")
                            .with(auth())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
            )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(ErrorCode.NPC_NOT_FOUND.getDescription()));
        }
    }

    @Nested
    @DisplayName("Context: 존재하지 않는 상점 아이템인 경우")
    class Context_shop_item_not_found {

        @Test
        @DisplayName("It: 404 NOT FOUND 반환")
        void It_상점아이템없음_실패() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("quantity", 1));
            mockMvc.perform(
                    MockMvcRequestBuilders
                            .post(BASE_ENDPOINT + "/" + testNpc.getNpcId() + "/items/999999/purchase")
                            .with(auth())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
            )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND.getDescription()));
        }
    }

    @Nested
    @DisplayName("Context: 인증 토큰이 없는 경우")
    class Context_without_token {

        @Test
        @DisplayName("It: 인증 없이 접근 시 리다이렉트 또는 401 반환")
        void It_인증없음_실패() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("quantity", 1));

            ResultActions actions = mockMvc.perform(
                    MockMvcRequestBuilders
                            .post(BASE_ENDPOINT + "/" + testNpc.getNpcId() + "/items/" + testNpcItem.getNpcItemId() + "/purchase")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
            );

            actions.andExpect(status().is(org.hamcrest.Matchers.either(
                    org.hamcrest.Matchers.is(401)).or(org.hamcrest.Matchers.is(302))
            ));
        }
    }
}
