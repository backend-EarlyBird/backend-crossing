package io.rapa.backendcrossing.npcs.repository;

/**
 * packageName    : io.rapa.backendcrossing.npcs.repository
 * fileName       : UserItemRepository
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : UserItem JPA Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

import io.rapa.backendcrossing.npcs.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    Optional<UserItem> findByUserUserIdAndItemItemId(Long userId, Long itemId);
}
