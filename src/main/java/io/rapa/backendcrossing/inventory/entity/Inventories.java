package io.rapa.backendcrossing.inventory.entity;

import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.users.domain.entity.Users;
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
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(name = "idx_unique_subuser_item", columnNames = {"sub_user_id", "item_id"}))
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

        private Long subUserId; // 시큐리티에서 가져온 정보 매핑용

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

        @ManyToOne
        @JoinColumn(
                name = "user_id",
                nullable = false,
                foreignKey = @ForeignKey(
                        name = "fk_users_inventories",
                        foreignKeyDefinition = "FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                )
        )
        private Users user;

        public void addQuantity(int quantity) {
            this.quantity += quantity;
        }

        //생성자
        @Builder
        public Inventories(
                Long userItemId,
                Long subUserId,
                Items item,
                int quantity,
                boolean equipped,
                String acquiredAt,
                Users user
        ) {
            this.userItemId = userItemId;
            this.subUserId = subUserId;
            this.item = item;
            this.quantity = quantity;
            this.equipped = equipped;
            this.acquiredAt = acquiredAt;
            this.user = user;
            this.user.addInventory(this);
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
