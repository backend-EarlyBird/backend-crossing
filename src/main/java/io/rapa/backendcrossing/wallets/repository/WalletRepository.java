package io.rapa.backendcrossing.wallets.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallets, Long> {

    default Wallets findByIdOrThrow(Long id){
        return findById(id).orElseThrow(
                ()-> new CustomException(ErrorCode.WALLET_NOT_FOUND)
        );
    }
}
