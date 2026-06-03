package io.rapa.backendcrossing.npcs.controller;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.npcs.controller
 * fileName       : NpcControllerIntegrationTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcController 통합 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
@DisplayName("Describe: NPC 조회 ( GET /api/v1/npcs )")
public class NpcControllerIntegrationTests {

    @Autowired MockMvc mockMvc;
    @Autowired NpcsRepository npcsRepository;

    final String BASE_ENDPOINT = "/api/v1/npcs";

    Npcs testNpc;

    @BeforeEach
    void setUp() {
        testNpc = npcsRepository.save(
                Npcs.builder().rId("npc_blacksmith").name("대장장이").locationKey("town").active(true).build()
        );
    }

    @Nested
    @DisplayName("Context: NPC 전체 조회")
    class Context_findAllNpcs {

        @Test
        @DisplayName("It: 200 OK와 함께 NPC 목록 반환")
        void It_전체조회_성공() throws Exception {
            ResultActions actions = mockMvc.perform(
                    MockMvcRequestBuilders.get(BASE_ENDPOINT + "/")
                            .contentType(MediaType.APPLICATION_JSON)
            );

            actions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("Context: NPC 단건 조회")
    class Context_getNpcInfo {

        @Test
        @DisplayName("It: 올바른 npcId - 200 OK와 함께 NPC 정보 반환")
        void It_단건조회_성공() throws Exception {
            ResultActions actions = mockMvc.perform(
                    MockMvcRequestBuilders.get(BASE_ENDPOINT + "/" + testNpc.getNpcId())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            actions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.npcId").value(testNpc.getNpcId()))
                    .andExpect(jsonPath("$.data.name").value("대장장이"))
                    .andExpect(jsonPath("$.data.rId").value("npc_blacksmith"));
        }

        @Test
        @DisplayName("It: 존재하지 않는 npcId - 404 NOT FOUND 반환")
        void It_단건조회_실패_NPC없음() throws Exception {
            ResultActions actions = mockMvc.perform(
                    MockMvcRequestBuilders.get(BASE_ENDPOINT + "/999999")
                            .contentType(MediaType.APPLICATION_JSON)
            );

            actions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(ErrorCode.NPC_NOT_FOUND.getDescription()));
        }
    }
}
