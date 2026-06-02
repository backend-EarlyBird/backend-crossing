package io.rapa.backendcrossing.npcs.entity;

import io.rapa.backendcrossing.items.entity.Items;
import jakarta.persistence.*;
import lombok.*;

/**
 * packageName    : io.rapa.backendcrossing.npcs.entity
 * fileName       : NpcItems
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : NpcItems Domain Entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NpcItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long npcItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id", nullable = false)
    private Npcs npc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Items item;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, name = "sort_order")
    private int sortOrder;
}
