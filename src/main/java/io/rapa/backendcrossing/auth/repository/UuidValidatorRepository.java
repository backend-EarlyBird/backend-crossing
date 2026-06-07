package io.rapa.backendcrossing.auth.repository;

import io.rapa.backendcrossing.auth.domain.entity.UuidValidator;
import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UuidValidatorRepository extends CrudRepository<UuidValidator, UUID> {
    boolean existsUuidValidatorById(UUID id);
    default UuidValidator findByIdOrThrow(UUID id){
        return findById(id).orElseThrow(
                ()-> new CustomException(ErrorCode.UUID_HISTORY_NOT_FOUND)
        );
    }
}
