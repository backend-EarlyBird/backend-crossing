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

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcItemsRepository extends JpaRepository<NpcItems, Long> {
    default NpcItems findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NPC_SHOP_ITEM_NOT_FOUND)
        );
    }
}
