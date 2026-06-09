package io.rapa.backendcrossing.friendRequests.repository;

import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.repository
 * fileName       : FriendRequestsRepository
 * author         : Admin
 * date           : 26. 6. 4.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@Repository
public interface FriendRequestsRepository extends JpaRepository<FriendRequests, Long>, FriendRepositoryCustom {

    List<FriendRequests> findByFromUser_UserIdAndToUser_UserIdAndStatus(Long fromUser_userId, Long toUser_userId, FriendRequestsStatus status);

    @Query("SELECT f FROM FriendRequests f WHERE (f.fromUser.userId = :userId OR f.toUser.userId = :userId) AND f.status = :status")
    List<FriendRequests> findFriendsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FriendRequestsStatus status);

    List<FriendRequests> findByToUserUserIdAndStatus(Long toUserId, FriendRequestsStatus status);

    Optional<FriendRequests> findByFriendRequestIdAndToUserUserId(Long friendRequestId, Long toUserId);

    Optional<FriendRequests> findByFriendRequestIdAndFromUserUserId(Long friendRequestId, Long fromUserId);

    Optional<FriendRequests> findByFromUserUserIdAndToUserUserIdAndStatus(
            Long fromUserId,
            Long toUserId,
            FriendRequestsStatus status
    );

    boolean existsByFromUserUserIdAndToUserUserIdAndStatus(
            Long fromUserId,
            Long toUserId,
            FriendRequestsStatus status
    );

    boolean existsByToUserUserIdAndFromUserUserIdAndStatus(
            Long toUserId,
            Long fromUserId,
            FriendRequestsStatus status
    );
}