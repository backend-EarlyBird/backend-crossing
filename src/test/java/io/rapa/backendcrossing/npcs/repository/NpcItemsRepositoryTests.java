package io.rapa.backendcrossing.npcs.repository;

import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.rapa.backendcrossing.common.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.autoconfigure.DataSourceInitializationAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.controller
 * fileName       : FriendRequestsControllerIntegrationTests
 * author         : Admin
 * date           : 26. 6. 7.
 * description    : NpcItemsRepositoryTests
 * - findByIdWithDetails (Fetch Join) 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 7.        Admin       최초 생성
 */
//Repository 테스트는 검증 대상이 쿼리 자체 이기 때문에 하나만함

/**
 * @Query findByIdWithDetails (Fetch Join) 테스트
 */
@DataJpaTest(excludeAutoConfiguration = DataSourceInitializationAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaConfig.class)
@DisplayName("NpcItemsRepository @Query 테스트")
class NpcItemsRepositoryTests {

    @Autowired
    private NpcItemsRepository npcItemsRepository;

    @Autowired
    private NpcsRepository npcsRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private TestEntityManager em;

    private Long savedNpcItemId;

    @BeforeEach
    void setUp() {
        Npcs npc = npcsRepository.save(Npcs.builder()
                .rId("npc_001")
                .name("상인")
                .locationKey("town")
                .active(true)
                .build());

        Items item = itemsRepository.save(Items.builder()
                .rId("sword_001")
                .itemName("연습용 검")
                .itemType(ItemType.WEAPON)
                .itemGrade(ItemGrade.COMMON)
                .price(100)
                .sellPrice(10)
                .build());

        NpcItems npcItem = npcItemsRepository.save(NpcItems.builder()
                .npc(npc)
                .item(item)
                .quantity(5)
                .sortOrder(1)
                .build());

        savedNpcItemId = npcItem.getNpcItemId();
        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화 → Lazy 로딩 강제
    }

    @Test
    @DisplayName("findByIdWithDetails - npc, item이 Fetch Join으로 함께 로딩됨")
    void findByIdWithDetails_fetchJoin() {
        Optional<NpcItems> result = npcItemsRepository.findByIdWithDetails(savedNpcItemId);

        assertThat(result).isPresent();
        // 영속성 컨텍스트 clear 후에도 LazyInitializationException 없이 접근 가능
        assertThat(result.get().getNpc().getName()).isEqualTo("상인");
        assertThat(result.get().getItem().getItemName()).isEqualTo("연습용 검");
    }

    @Test
    @DisplayName("findByIdWithDetails - 존재하지 않는 id는 empty 반환")
    void findByIdWithDetails_notFound() {
        Optional<NpcItems> result = npcItemsRepository.findByIdWithDetails(999999L);

        assertThat(result).isEmpty();
    }
}
