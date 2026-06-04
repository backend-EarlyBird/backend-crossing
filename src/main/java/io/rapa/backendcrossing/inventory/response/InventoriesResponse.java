package io.rapa.backendcrossing.inventory.response;

import io.rapa.backendcrossing.inventory.entity.Inventories;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "인벤토리 상세 정보 응답 DTO")
public class InventoriesResponse {

    @Schema(description = "인벤토리 아이템 고유 ID", example = "1")
    private Long userItemId;
    @Schema(description = "아이템 ID", example = "1")
    private Long itemId;
    @Schema(description = "리소스 ID", example = "potion_hp_001")
    private String rId;
    @Schema(description = "아이템 이름", example = "HP 포션")
    private String itemName;
    @Schema(description = "아이템 타입", example = "CONSUMABLE")
    private ItemType itemType;
    @Schema(description = "아이템 등급", example = "COMMON")
    private ItemGrade itemGrade;
    @Schema(description = "설명", example = "HP를 50 회복합니다.")
    private String description;
    @Schema(description = "구매 가격", example = "30")
    private Integer price;
    @Schema(description = "판매 가격", example = "10")
    private Integer sellPrice;
    @Schema(description = "보유 수량", example = "5")
    private Integer quantity;
    @Schema(description = "장착 여부", example = "false")
    private boolean equipped;
    @Schema(description = "획득 시간", example = "2026-05-21T12:00:00")
    private String acquiredAt;

    public static InventoriesResponse from(Inventories inventory) {
        Items item = inventory.getItem();
        return InventoriesResponse.builder()
                .userItemId(inventory.getUserItemId())
                .itemId(item.getItemId())
                .rId(item.getRId())
                .itemName(item.getItemName())
                .itemType(item.getItemType())
                .itemGrade(item.getItemGrade())
                .description(item.getDescription())
                .price(item.getPrice())
                .sellPrice(item.getSellPrice())
                .quantity(inventory.getQuantity())
                .equipped(inventory.isEquipped())
                .acquiredAt(inventory.getAcquiredAt())
                .build();
    }
}
