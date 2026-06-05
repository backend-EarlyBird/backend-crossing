package io.rapa.backendcrossing.users.repository;

import io.rapa.backendcrossing.profiles.domain.entity.Profiles;
import io.rapa.backendcrossing.profiles.repository.ProfileRepository;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserBoundaryRepositoryImpl implements UserBoundaryRepository {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final ProfileRepository profilesRepository;

    @Override
    public Users findUserByIdOrThrow(Long userId) {
        return userRepository.findByIdOrThrow(userId);
    }

    @Override
    public Optional<Users> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Users findUserByEmailOrThrow(String email) {
        return userRepository.findByEmailOrThrow(email);
    }

    @Override
    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Wallets findWalletByUserIdOrThrow(Long userId) {
        return walletRepository.findByUserIdOrThrow(userId);
    }

    @Override
    public Wallets saveWallet(Wallets wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    public Profiles saveProfile(Profiles profiles) {
        return profilesRepository.save(profiles);
    }
}
