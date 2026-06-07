package io.rapa.backendcrossing.friendRequests.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


@DataJpaTest
class FriendRepositoryTest {

    @Autowired
    FriendRepository friendRepository;

    @Nested
    @DisplayName("Describe : countFriendByUserId()")
    class Describe_with_{
        
    }

}