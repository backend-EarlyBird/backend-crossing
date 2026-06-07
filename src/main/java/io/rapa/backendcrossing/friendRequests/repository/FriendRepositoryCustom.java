package io.rapa.backendcrossing.friendRequests.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepositoryCustom {
    Integer countFriendByUserId(Long userId);
}
