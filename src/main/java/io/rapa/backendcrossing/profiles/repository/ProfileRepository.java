package io.rapa.backendcrossing.profiles.repository;

import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profiles, Long> {
}
