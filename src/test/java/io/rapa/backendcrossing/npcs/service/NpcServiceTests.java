package io.rapa.backendcrossing.npcs.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.response.NpcsResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * packageName    : io.rapa.backendcrossing.npcs.service
 * fileName       : NpcServiceTests
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcService 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@ActiveProfiles("test")
@DisplayName("NpcService 단위 테스트")
public class NpcServiceTests {

    @Mock
    private NpcsRepository npcsRepository;

    @InjectMocks
    private NpcService npcService;

    @Test
    @DisplayName("NPC 전체 조회 - 성공")
    void findAllNpcs() {
        // given
        Npcs npc1 = Npcs.builder().npcId(1L).rId("npc_blacksmith").name("대장장이").locationKey("town").active(true).build();
        Npcs npc2 = Npcs.builder().npcId(2L).rId("npc_merchant").name("상인").locationKey("market").active(true).build();

        given(npcsRepository.findAll()).willReturn(List.of(npc1, npc2));

        // when
        List<NpcsResponse> result = npcService.findAllNpcs();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("대장장이");
        verify(npcsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("NPC 단건 조회 - 성공")
    void getNpcInfo() {
        // given
        Long npcId = 1L;
        Npcs npc = Npcs.builder().npcId(npcId).rId("npc_blacksmith").name("대장장이").locationKey("town").active(true).build();

        given(npcsRepository.findByIdOrThrow(npcId)).willReturn(npc);

        // when
        NpcsResponse result = npcService.getNpcInfo(npcId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNpcId()).isEqualTo(npcId);
        assertThat(result.getName()).isEqualTo("대장장이");
    }

    @Test
    @DisplayName("NPC 단건 조회 - 실패 (존재하지 않는 NPC)")
    void getNpcInfo_Fail() {
        // given
        Long invalidId = 999L;

        given(npcsRepository.findByIdOrThrow(invalidId))
                .willThrow(new CustomException(ErrorCode.NPC_NOT_FOUND));

        // when / then
        CustomException exception = assertThrows(CustomException.class, () ->
                npcService.getNpcInfo(invalidId)
        );

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NPC_NOT_FOUND.getDescription());
    }
}
