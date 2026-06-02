package io.rapa.backendcrossing.items.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.entity.Items;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName    : io.rapa.backendcrossing.repository
 * fileName       : ItemResitory
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : Items JPA Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */

public interface ItemRepository extends JpaRepository<Items, Long> {
    // Repository에서 예외 처리까지 공통화!
    default Items findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.ITEM_NOT_FOUND)
        );
    }
}
