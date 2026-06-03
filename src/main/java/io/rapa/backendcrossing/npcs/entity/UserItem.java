package io.rapa.backendcrossing.npcs.entity;

/**
 * packageName    : io.rapa.backendcrossing.npcs.entity
 * fileName       : UserItem
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : UserItem Domain Entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.users.domain.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Items item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    @Builder.Default
    private boolean equipped = false;

    @Column(nullable = false)
    private LocalDateTime acquiredAt;

    public void addQuantity(int amount) {
        this.quantity += amount;
    }
}
