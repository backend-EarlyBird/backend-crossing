package io.rapa.backendcrossing.npcs.response;

import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : io.rapa.backendcrossing.npcs.response;
 * fileName       : NpcItemsResponse
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : NpcShopItem JPA Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "NPC 상점 아이템 응답 DTO")
public class NpcItemsResponse {

    @Schema(description = "NPC 상점 아이템 ID", example = "1")
    private Long npcItemId;
    @Schema(description = "아이템 ID", example = "1")
    private Long itemId;
    @Schema(description = "리소스 ID", example = "sword_001")
    private String rId;
    @Schema(description = "아이템 이름", example = "연습용 검")
    private String itemName;
    @Schema(description = "아이템 타입", example = "WEAPON")
    private ItemType itemType;
    @Schema(description = "아이템 등급", example = "COMMON")
    private ItemGrade itemGrade;
    @Schema(description = "설명", example = "초보자용 검입니다.")
    private String description;
    @Schema(description = "구매 가격", example = "100")
    private Integer price;
    @Schema(description = "판매 가격", example = "50")
    private Integer sellPrice;
    @Schema(description = "재고 수량", example = "10")
    private Integer quantity;
    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    public static NpcItemsResponse from(NpcItems shopItem) {
        var item = shopItem.getItem();
        return NpcItemsResponse.builder()
                .npcItemId(shopItem.getNpcItemId())
                .itemId(item.getItemId())
                .rId(item.getRId())
                .itemName(item.getItemName())
                .itemType(item.getItemType())
                .itemGrade(item.getItemGrade())
                .description(item.getDescription())
                .price(item.getPrice())
                .sellPrice(item.getSellPrice())
                .quantity(shopItem.getQuantity())
                .sortOrder(shopItem.getSortOrder())
                .build();
    }
}
