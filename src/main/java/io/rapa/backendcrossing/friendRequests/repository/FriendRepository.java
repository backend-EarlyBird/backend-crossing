package io.rapa.backendcrossing.friendRequests.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository {

    Integer countFriendByUserId(Long userId);

}
