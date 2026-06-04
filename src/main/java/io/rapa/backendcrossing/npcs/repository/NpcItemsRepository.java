package io.rapa.backendcrossing.npcs.repository;

/**
 * packageName    : io.rapa.backendcrossing.npcs.repository
 * fileName       : NpcItemsRepository
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : NpcItems JPA Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

import io.lettuce.core.dynamic.annotation.Param;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NpcItemsRepository extends JpaRepository<NpcItems, Long> {

    // Fetch Join을 사용하여 연관 엔티티를 한 번에 로딩
    // NpcItems + Npc + Item : 1+N 관련 해결용
    @Query("SELECT ni FROM NpcItems ni JOIN FETCH ni.npc JOIN FETCH ni.item WHERE ni.npcItemId = :id")
    Optional<NpcItems> findByIdWithDetails(@Param("id") Long id);

    default NpcItems findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND)
        );
    }
}
