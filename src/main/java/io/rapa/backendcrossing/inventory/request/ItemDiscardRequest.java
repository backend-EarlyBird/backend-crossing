package io.rapa.backendcrossing.inventory.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDiscardRequest {
    private Long userItemId;
    private Integer quantity;
}
