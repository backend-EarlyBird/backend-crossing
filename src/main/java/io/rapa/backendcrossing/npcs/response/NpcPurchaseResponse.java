package io.rapa.backendcrossing.npcs.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * packageName    : io.rapa.backendcrossing.npcs.response;
 * fileName       : NpcItemsResponse
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcPurchase JPA Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

@Getter
@Builder
@Schema(description = "NPC 상점 아이템 구매 응답 DTO")
public class NpcPurchaseResponse {

    @Schema(description = "구매 후 지갑 상태")
    private WalletDto wallet;

    @Schema(description = "획득한 아이템")
    private AcquiredItemDto acquiredItem;

    @Getter
    @Builder
    public static class WalletDto {
        @Schema(description = "남은 골드", example = "4910")
        private Long gold;
        @Schema(description = "남은 보석", example = "10")
        private Long gem;
    }

    @Getter
    @Builder
    public static class AcquiredItemDto {
        @Schema(description = "유저 아이템 ID", example = "2")
        private Long userItemId;
        @Schema(description = "아이템 ID", example = "2")
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
        @Schema(description = "수량", example = "3")
        private Integer quantity;
        @Schema(description = "장착 여부", example = "false")
        private boolean equipped;
        @Schema(description = "획득 시각", example = "2026-05-21T12:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime acquiredAt;
    }
}
