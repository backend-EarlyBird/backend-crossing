package io.rapa.backendcrossing.wallets.domain.entity;

/**
 * packageName    : io.rapa.backendcrossing.npcs.entity
 * fileName       : Wallet
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : Wallet Domain Entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

import io.rapa.backendcrossing.users.domain.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false)
    private Long gold;

    @Column(nullable = false)
    private Long gem;

    @Builder
    public Wallets(
            Long gold,
            Long gem,
            Users user
    ){
        this.gold = gold;
        this.gem = gem;
        this.user = user;
    }

    public void deductGold(long amount) {
        this.gold -= amount;
    }
}
