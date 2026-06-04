package io.rapa.backendcrossing.inventory.entity;

import io.rapa.backendcrossing.items.entity.Items;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

/**
 * packageName    : io.rapa.backendcrossing.inventory.entity
 * fileName       : Inventories
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : Inventories Domain Entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 규약에 따른 기본 생성자
public class Inventories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userItemId;

    //private Long itemId;


   /* private String rId;
    private String itemName;
    private String itemType;
    private String itemGrade;
    private String description;
    private int price;
    private int sellPrice;*/

    private Long userId; // 시큐리티에서 가져온 정보 매핑용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Items item; // Items 엔티티와 관계 설정


    @Column(nullable = false)
    private int quantity;

    @ColumnDefault("0") // 기본값 false지정
    @Column(name = "equipped", columnDefinition = "TINYINT(1)") //이러면 true 1, false 0으로 자동으로 저장된다함
    private boolean equipped;

    @CreationTimestamp // 객체 생성시 현재 시간 자동 저장
    @Column(nullable = false, updatable = false) // 수정 불가
    private String acquiredAt;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    /*
    {
    "userItemId": 1,
    "itemId": 1,
    "rId": "sword_001",
    "itemName": "연습용 검",
    "itemType": "WEAPON",
    "itemGrade": "COMMON",
    "description": "초보자용 검입니다.",
    "price": 100,
    "sellPrice": 50,
    "quantity": 1,
    "equipped": true,
    "acquiredAt": "2025-01-01T00:00:00"
}
    * */
}
