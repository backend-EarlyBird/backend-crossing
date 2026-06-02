package io.rapa.backendcrossing.inventory.controller;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.inventory.repository.InventoriesRepository;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.constants.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.inventory.controller
 * fileName       : InventoryControllerIntegrationTests
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : InventoriesController 통합 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@DisplayName("InventoriesController 통합 테스트")
@Slf4j
public class InventoryControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoriesRepository inventoriesRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    private static final Long USER_ID = 1L;
    private Items savedItem;

    @BeforeEach
    void setUp() {
        CurrentUser currentUser = CurrentUser.builder()
                .nickName("테스트유저")
                .email("test@test.com")
                .build()
                .setId(USER_ID)
                .setRole(Role.USER);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities())
        );

        savedItem = itemsRepository.save(Items.builder()
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
    void getInventories() throws Exception {
        // given
        inventoriesRepository.save(Inventories.builder()
                .userId(USER_ID).item(savedItem).quantity(3).equipped(false).build());

        // when & then
        mockMvc.perform(get("/api/v1/users/me/inventory/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].quantity").value(3));
    }

    @Test
    @DisplayName("인벤토리 조회 - 빈 인벤토리")
    void getInventories_empty() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/inventory/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("아이템 획득 - 새 아이템 저장")
    void pickupItem_newItem() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/inventory/pickup")
                        .param("itemId", savedItem.getItemId().toString())
                        .param("quantity", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("아이템 획득 - 존재하지 않는 아이템 ID")
    void pickupItem_itemNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/users/me/inventory/pickup")
                        .param("itemId", "9999")
                        .param("quantity", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
