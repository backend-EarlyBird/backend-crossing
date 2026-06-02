package io.rapa.backendcrossing.users.repository;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findById(Long id);
    default Users findByIdOrThrow(Long id){
       return findById(id).orElseThrow(
               ()->new CustomException(ErrorCode.USER_NOT_FOUND, "해당 ID를 가진 사용자 계정을 찾을 수 없습니다.")
       );
    }
    Optional<Users> findByEmail(String email);
    default Users findByEmailOrThrow(String email){
        return findByEmail(email).orElseThrow(
                ()->new CustomException(ErrorCode.USER_NOT_FOUND, "해당 이메일을 가진 사용자 계정을 찾을 수 없습니다.")
        );
    }
}
