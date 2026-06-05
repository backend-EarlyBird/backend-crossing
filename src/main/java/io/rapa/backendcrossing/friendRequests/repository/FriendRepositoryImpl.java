package io.rapa.backendcrossing.friendRequests.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.QFriendRequests;
import io.rapa.backendcrossing.users.domain.entity.QUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QFriendRequests qFriendRequests = QFriendRequests.friendRequests;

    @Override
    public Integer countFriendByUserId(Long userId) {
        return jpaQueryFactory.selectFrom(qFriendRequests)
                .where(
                        qFriendRequests.fromUser.userId.eq(userId)
                                .and(qFriendRequests.status.eq(FriendRequestsStatus.ACCEPTED))
                ).fetch().size();
    }
}
