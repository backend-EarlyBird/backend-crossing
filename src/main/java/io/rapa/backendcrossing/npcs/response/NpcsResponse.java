package io.rapa.backendcrossing.npcs.response;

import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName    : io.rapa.backendcrossing.npcs.response;
 * fileName       : NpcsResponse
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : Npcs JPA Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "NPC 상세 정보 응답 DTO")
public class NpcsResponse {

    @Schema(description = "NPC ID", example = "1")
    private Long npcId;
    @Schema(description = "리소스 ID", example = "npc_blacksmith")
    private String rId;
    @Schema(description = "NPC 이름", example = "대장장이")
    private String name;
    @Schema(description = "설명", example = "무기를 판매하는 대장장이입니다.")
    private String description;
    @Schema(description = "위치 키", example = "town_center")
    private String locationKey;
    @Schema(description = "활성화 여부", example = "true")
    private Boolean active;
    @Schema(description = "상점 아이템 목록")
    private List<NpcItemsResponse> shopItems;

    public static NpcsResponse from(Npcs npc) {
        return NpcsResponse.builder()
                .npcId(npc.getNpcId())
                .rId(npc.getRId())
                .name(npc.getName())
                .description(npc.getDescription())
                .locationKey(npc.getLocationKey())
                .active(npc.getActive())
                .shopItems(npc.getShopItems().stream().map(NpcItemsResponse::from).toList())
                .build();
    }
}
