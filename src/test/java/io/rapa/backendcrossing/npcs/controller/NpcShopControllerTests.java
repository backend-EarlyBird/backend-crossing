package io.rapa.backendcrossing.npcs.controller;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
import io.rapa.backendcrossing.npcs.service.NpcShopService;
import io.rapa.backendcrossing.npcs.util.WithMockCurrentUser;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.npcs.controller
 * fileName       : NpcShopControllerTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcShopController 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@WebMvcTest(NpcShopController.class)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
@DisplayName("NpcShopController 단위 테스트")
public class NpcShopControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean NpcShopService npcShopService;
    @MockitoBean TokenService tokenService;
    @MockitoBean UserService userService;

    final String BASE_ENDPOINT = "/api/v1/users/me/npcs";

    private NpcPurchaseResponse mockResponse() {
        return NpcPurchaseResponse.builder()
                .wallet(NpcPurchaseResponse.WalletDto.builder().gold(4910L).gem(10L).build())
                .acquiredItem(NpcPurchaseResponse.AcquiredItemDto.builder()
                        .userItemId(1L).itemId(2L).rId("potion_hp_001").itemName("HP 포션")
                        .itemType(ItemType.CONSUMABLE).itemGrade(ItemGrade.COMMON)
                        .description("HP를 50 회복합니다.").price(30).sellPrice(10)
                        .quantity(3).equipped(false).acquiredAt(LocalDateTime.now())
                        .build())
                .build();
    }

    @Test
    @DisplayName("구매 - 성공")
    @WithMockCurrentUser
    void purchase_Success() throws Exception {
        given(npcShopService.purchase(any(), eq(1L), eq(1L), any())).willReturn(mockResponse());

        String body = objectMapper.writeValueAsString(Map.of("quantity", 3));

        mockMvc.perform(post(BASE_ENDPOINT + "/1/items/1/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(SuccessMessage.NPC_PURCHASE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.wallet.gold").value(4910))
                .andExpect(jsonPath("$.data.acquiredItem.quantity").value(3));
    }

    @Test
    @DisplayName("구매 - 실패 (골드 부족 400)")
    @WithMockCurrentUser
    void purchase_Fail_InsufficientGold() throws Exception {
        given(npcShopService.purchase(any(), eq(1L), eq(1L), any()))
                .willThrow(new CustomException(ErrorCode.INSUFFICIENT_GOLD));

        String body = objectMapper.writeValueAsString(Map.of("quantity", 3));

        mockMvc.perform(post(BASE_ENDPOINT + "/1/items/1/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.INSUFFICIENT_GOLD.getDescription()));
    }

    @Test
    @DisplayName("구매 - 실패 (NPC 없음 404)")
    @WithMockCurrentUser
    void purchase_Fail_NpcNotFound() throws Exception {
        given(npcShopService.purchase(any(), eq(999L), eq(1L), any()))
                .willThrow(new CustomException(ErrorCode.NPC_NOT_FOUND));

        String body = objectMapper.writeValueAsString(Map.of("quantity", 1));

        mockMvc.perform(post(BASE_ENDPOINT + "/999/items/1/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.NPC_NOT_FOUND.getDescription()));
    }

    @Test
    @DisplayName("구매 - 실패 (상점 아이템 없음 404)")
    @WithMockCurrentUser
    void purchase_Fail_ShopItemNotFound() throws Exception {
        given(npcShopService.purchase(any(), eq(1L), eq(999L), any()))
                .willThrow(new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND));

        String body = objectMapper.writeValueAsString(Map.of("quantity", 1));

        mockMvc.perform(post(BASE_ENDPOINT + "/1/items/999/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND.getDescription()));
    }
}
