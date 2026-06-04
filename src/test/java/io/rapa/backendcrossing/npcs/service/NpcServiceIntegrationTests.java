package io.rapa.backendcrossing.npcs.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.response.NpcsResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * packageName    : io.rapa.backendcrossing.npcs.service
 * fileName       : NpcServiceIntegrationTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcService 통합 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@SpringBootTest
@Transactional
@Slf4j
@DisplayName("NpcService 통합 테스트")
public class NpcServiceIntegrationTests {

    @Autowired
    private NpcsRepository npcsRepository;

    @Autowired
    private NpcService npcService;

    @Test
    @DisplayName("NPC 전체 조회 - 성공")
    void findAllNpcs() {
        // given
        npcsRepository.save(Npcs.builder().rId("npc_blacksmith").name("대장장이").locationKey("town").active(true).build());
        npcsRepository.save(Npcs.builder().rId("npc_merchant").name("상인").locationKey("market").active(true).build());

        // when
        List<NpcsResponse> result = npcService.findAllNpcs();

        // then
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("NPC 단건 조회 - 성공")
    void getNpcInfo() {
        // given
        Npcs saved = npcsRepository.save(
                Npcs.builder().rId("npc_blacksmith").name("대장장이").locationKey("town").active(true).build()
        );

        // when
        NpcsResponse result = npcService.getNpcInfo(saved.getNpcId());

        // then
        assertThat(result.getName()).isEqualTo("대장장이");
        assertThat(result.getRId()).isEqualTo("npc_blacksmith");
    }

    @Test
    @DisplayName("NPC 단건 조회 - 실패 (존재하지 않는 NPC)")
    void getNpcInfo_Fail() {
        // given
        Long invalidId = 999999L;

        // when / then
        CustomException exception = assertThrows(CustomException.class, () ->
                npcService.getNpcInfo(invalidId)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NPC_NOT_FOUND.getDescription());
    }
}
