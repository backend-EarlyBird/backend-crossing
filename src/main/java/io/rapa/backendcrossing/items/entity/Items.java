package io.rapa.backendcrossing.items.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Items extends BaseEntity { // BaseEntity 상속 (생성일, 수정일 등 공통 필드)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String rId;
    private String itemName;
    private String itemType;
    private String itemGrade;
    private String description;
    private int price;
    private int sellPrice;
}