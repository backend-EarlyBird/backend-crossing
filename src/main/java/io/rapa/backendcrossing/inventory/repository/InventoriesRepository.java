package io.rapa.backendcrossing.inventory.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.inventory.entity.Inventories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : io.rapa.backendcrossing.inventory.repository
 * fileName       : InventoriesRepository
 * author         : Admin
 * date           : 26. 6. 2.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
public interface InventoriesRepository extends JpaRepository<Inventories, Long> {

    // 유저의 인벤토리 리스트 조회
    List<Inventories> findBySubUserId(Long userId);

    // 유저의 특정 아이템 조회
    Optional<Inventories> findBySubUserIdAndItemItemId(Long userId, Long itemId);

    // userId 없으면 401
    default List<Inventories> findByUserIdOrThrow(Long userId) {
        if (userId == null) throw new CustomException(ErrorCode.INVENTORY_UNAUTHORIZED);
        return findBySubUserId(userId);
    }

}
