package io.rapa.backendcrossing.npcs.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.npcs.response.NpcsResponse;
import io.rapa.backendcrossing.npcs.service.NpcService;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.npcs.controller
 * fileName       : NpcControllerTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcController 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@WebMvcTest(NpcController.class)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
@DisplayName("NpcController 단위 테스트")
public class NpcControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NpcService npcService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("NPC 전체 조회 - 성공")
    void findAllNpcs() throws Exception {
        given(npcService.findAllNpcs()).willReturn(List.of(
                NpcsResponse.builder().npcId(1L).name("대장장이").rId("npc_blacksmith").locationKey("town").active(true).build(),
                NpcsResponse.builder().npcId(2L).name("상인").rId("npc_merchant").locationKey("market").active(true).build()
        ));

        mockMvc.perform(get("/api/v1/npcs/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("대장장이"));
    }

    @Test
    @DisplayName("NPC 단건 조회 - 성공")
    void getNpcInfo() throws Exception {
        given(npcService.getNpcInfo(1L)).willReturn(
                NpcsResponse.builder().npcId(1L).name("대장장이").rId("npc_blacksmith").locationKey("town").active(true).build()
        );

        mockMvc.perform(get("/api/v1/npcs/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.npcId").value(1))
                .andExpect(jsonPath("$.data.name").value("대장장이"));
    }

    @Test
    @DisplayName("NPC 단건 조회 - 실패 (404)")
    void getNpcInfo_Fail() throws Exception {
        given(npcService.getNpcInfo(999L))
                .willThrow(new CustomException(ErrorCode.NPC_NOT_FOUND));

        mockMvc.perform(get("/api/v1/npcs/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(ErrorCode.NPC_NOT_FOUND.getDescription()));
    }
}
