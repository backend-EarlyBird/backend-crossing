package io.rapa.backendcrossing.users.repository;

import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBoundaryRepository {

    // 유저
    Users findUserByIdOrThrow(Long userId);
    Optional<Users> findUserByEmail(String email);
    Users findUserByEmailOrThrow(String email);
    Users saveUser(Users user);
    boolean existsUserById(Long userId);


    Wallets findWalletByUserIdOrThrow(Long userId);
    Wallets saveWallet(Wallets wallet);

    Profiles saveProfile(Profiles profiles);
    Profiles findProfileByUserIdOrThrow(Long userId);
}
