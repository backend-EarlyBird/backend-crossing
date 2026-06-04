package io.rapa.backendcrossing.npcs.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : io.rapa.backendcrossing.npcs.entity
 * fileName       : Npcs
 * author         : Admin
 * date           : 26. 6. 2.
 * description    :  Npcs Domain Entity
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
public class Npcs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long npcId;

    @Column(nullable = false, length = 50, name = "r_id")
    private String rId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 100, name = "location_key")
    private String locationKey;

    @ColumnDefault("1") // 기본값 true지정
    @Column(name = "active", columnDefinition = "TINYINT(1)") //이러면 true 1, false 0으로 자동으로 저장된다함
    private Boolean active;

    @Builder.Default
    @OneToMany(mappedBy = "npc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NpcItems> shopItems = new ArrayList<>();
}