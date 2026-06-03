package io.rapa.backendcrossing.npcs.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NpcsRepository extends JpaRepository<Npcs, Long> {
    default Npcs findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NPC_NOT_FOUND)
        );
    }
}
