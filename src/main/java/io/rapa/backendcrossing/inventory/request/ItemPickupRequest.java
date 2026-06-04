package io.rapa.backendcrossing.inventory.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "아이템 픽업 요청 DTO")
public class ItemPickupRequest {

    @Schema(description = "아이템 ID", example = "1")
    private Long itemId;

    @Schema(description = "수량", example = "1")
    private Integer quantity;
}
