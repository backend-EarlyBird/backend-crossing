package io.rapa.backendcrossing.inventory.controller;

import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.inventory.service.InventoriesService;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.constants.Role;
import io.rapa.backendcrossing.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rapa.backendcrossing.inventory.request.ItemPickupRequest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.inventory.controller
 * fileName       : InventoryControllerTests
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : InventoriesController 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@WebMvcTest(InventoriesController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("InventoriesController 단위 테스트")
@Slf4j
@ActiveProfiles("test")
public class InventoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoriesService inventoriesService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserService userService;


    @BeforeEach
    void setUpSecurityContext() {
        CurrentUser currentUser = CurrentUser.builder()
                .nickName("테스트유저")
                .email("test@test.com")
                .build()
                .setId(1L)
                .setRole(Role.USER);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities())
        );
    }

    @Test
    @DisplayName("인벤토리 조회 - 성공")
    void getInventories() throws Exception {
        // given
        given(inventoriesService.getInventory(1L)).willReturn(List.of(
                InventoriesResponse.builder().userItemId(1L).quantity(3).equipped(false).build()
        ));

        // when & then
        mockMvc.perform(get("/api/v1/users/me/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].quantity").value(3));
    }

    @Test
    @DisplayName("아이템 획득 - 성공")
    void pickupItem() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/users/me/inventory/pickup")
                        .content(new ObjectMapper().writeValueAsString(ItemPickupRequest.builder().itemId(1L).quantity(2).build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(inventoriesService).pickupItem(1L, 2, 1L);
    }

    @Test
    @DisplayName("아이템 버림 - 성공")
    void discardItem_success() throws Exception {
        // given
        given(inventoriesService.discardItem(1L, 2, 1L)).willReturn(
                InventoriesResponse.builder().userItemId(1L).quantity(0).equipped(false).build()
        );

        // when & then
        mockMvc.perform(delete("/api/v1/users/me/inventory/1/discard")
                        .param("quantity", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(inventoriesService).discardItem(1L, 2, 1L);
    }

    @Test
    @DisplayName("아이템 버림 - 서비스 호출 확인")
    void discardItem_verifiesServiceCall() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/users/me/inventory/5/discard")
                        .param("quantity", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(inventoriesService).discardItem(5L, 3, 1L);
    }
}
