package io.rapa.backendcrossing.inventory.response;

import io.rapa.backendcrossing.items.entity.Items;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * packageName    : io.rapa.backendcrossing.inventory.response
 * fileName       : InventoriesResponse
 * author         : Admin
 * date           : 26. 6. 2.
 * description    :Inventories request
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "인벤토리 상세 정보 응답 DTO")
@Getter
@Setter
public class InventoriesResponse {

    @Schema(description = "인벤토리 아이템 고유 ID", example = "1")
    private Long userItemId;

    @Schema(description = "아이템 상세 정보")
    private Items item;

    @Schema(description = "보유 수량", example = "5")
    private int quantity;

    @Schema(description = "장착 여부", example = "true")
    private boolean equipped;

    @Schema(description = "획득 시간", example = "2026-06-02T10:03:00")
    private String acquiredAt;
}
