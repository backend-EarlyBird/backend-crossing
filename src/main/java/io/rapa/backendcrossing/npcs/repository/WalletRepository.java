package io.rapa.backendcrossing.npcs.repository;

/**
 * packageName    : io.rapa.backendcrossing.npcs.repository
 * fileName       : WalletRepository
 * author         : Admin
 * date           : 26. 6. 3.
 * description    : Wallet JPA Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        Admin       최초 생성
 */

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallets, Long> {
    Optional<Wallets> findByUserUserId(Long userId);

    default Wallets findByUserIdOrThrow(Long userId) {
        return findByUserUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
