package io.rapa.backendcrossing.items.request;

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
public class ItemsRequest {

    private Long itemId;

    private String rId;
    private String itemName;
    private String itemType;
    private String itemGrade;
    private String description;
    private int price;
    private int sellPrice;

}
