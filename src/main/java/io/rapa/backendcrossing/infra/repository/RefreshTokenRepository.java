package io.rapa.backendcrossing.infra.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.infra.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    default RefreshToken findByRefreshTokenByIdOrThrow(String refreshToken) {
        return findById(refreshToken).orElseThrow(()->new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }
    boolean existsRefreshTokenByRefreshToken(String refreshToken);

    Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken);
}
