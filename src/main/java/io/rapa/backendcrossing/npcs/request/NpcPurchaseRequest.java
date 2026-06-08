package io.rapa.backendcrossing.npcs.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : io.rapa.backendcrossing.npcs.response;
 * fileName       : NpcItemsResponse
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcPurchaseRequest Request
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */


@Getter
@NoArgsConstructor
@Schema(description = "NPC 상점 아이템 구매 요청 DTO")
public class NpcPurchaseRequest {

    @Min(1)
    @Schema(description = "구매 수량", example = "3")
    private Integer quantity;

    public NpcPurchaseRequest(Integer quantity){
        this.quantity = quantity;
    }
}
