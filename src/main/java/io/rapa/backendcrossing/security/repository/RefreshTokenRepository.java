package io.rapa.backendcrossing.security.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.security.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    default RefreshToken findByRefreshTokenByIdOrThrow(String refreshToken) {
        return findById(refreshToken).orElseThrow(()->new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    Optional<RefreshToken> findById(String refreshToken);
}
