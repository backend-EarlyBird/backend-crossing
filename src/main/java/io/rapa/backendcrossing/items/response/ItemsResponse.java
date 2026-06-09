package io.rapa.backendcrossing.items.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : io.rapa.backendcrossing.domain.request
 * fileName       : Items
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : Items request
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "아이템 상세 정보 응답 DTO")
public class ItemsResponse {

    @Schema(description = "아이템 ID", example = "1")
    private Long itemId;
    @Schema(description = "리소스 ID", example = "sword_001")
    private String rId;
    @Schema(description = "아이템 이름", example = "연습용 검")
    private String itemName;
    @Schema(description = "아이템 타입", example = "WEAPON")
    private String itemType;
    @Schema(description = "아이템 등급", example = "COMMON")
    private String itemGrade;
    @Schema(description = "설명", example = "초보자용 검입니다.")
    private String description;
    @Schema(description = "구매 가격", example = "100")
    private Integer price;
    @Schema(description = "판매 가격", example = "50")
    private Integer sellPrice;

}
