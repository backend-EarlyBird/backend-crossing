package io.rapa.backendcrossing.items.entity;

import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import jakarta.persistence.*;
import lombok.*;

/**
 * packageName    : io.rapa.backendcrossing.domain.entity
 * fileName       : Items
 * author         : Admin
 * date           : 26. 6. 1.
 * description    :  Items Domain Entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 규약에 따른 기본 생성자
public class Items { // BaseEntity 상속 (생성일, 수정일 등 공통 필드)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false, length = 50, name = "r_id")
    private String rId;

    @Column(nullable = false, length = 50, name = "item_name")
    private String itemName;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ItemType itemType;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ItemGrade itemGrade;

    @Column(nullable = true, length = 255)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, name = "sell_price")
    private int sellPrice;
}