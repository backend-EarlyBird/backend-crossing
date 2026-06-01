package io.rapa.backendcrossing.repository;

import io.rapa.backendcrossing.constant.ErrorCode;
import io.rapa.backendcrossing.domain.entity.Users;
import io.rapa.backendcrossing.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findById(Long id);
    default Users findByIdOrThrow(Long id){
       return findById(id).orElseThrow(
               ()->new CustomException(ErrorCode.USER_NOT_FOUND, "해당 이메일로 사용자 계정을 찾을 수 없습니다.")
       );
    }
    Optional<Users> findByEmail(String email);
    default Users findByEmailOrThrow(String email){
        return findByEmail(email).orElseThrow(
                ()->new CustomException(ErrorCode.USER_NOT_FOUND, "해당 이메일로 사용자 계정을 찾을 수 없습니다.")
        );
    }
}
