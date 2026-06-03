package io.rapa.backendcrossing.npcs.entity;

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
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false)
    @Builder.Default
    private Long gold = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long gem = 0L;

    public void deductGold(long amount) {
        this.gold -= amount;
    }
}
