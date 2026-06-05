package io.rapa.backendcrossing.profiles.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profiles, Long> {
    default Profiles findByIdOrThrow(Long id){
        return findById(id).orElseThrow(
                ()-> new CustomException(ErrorCode.PROFILE_NOT_FOUND)
        );
    }
}
