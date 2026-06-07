package io.rapa.backendcrossing.friendRequests.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import io.rapa.backendcrossing.friendRequests.reponse.FriendRequestResponse;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.service
 * fileName       : FriendRequestsService
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsService 서비스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@Service
@RequiredArgsConstructor
public class FriendRequestsService {

    private final FriendRequestsRepository friendRequestsRepository;
    private final UserRepository userRepository;

    /**
     * 친구 목록 조회
     * 내가 보냈거나 받은 요청 중 ACCEPTED 상태인 요청 목록 조회 (상대방 정보 기준)
     */
    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getFriends(Long userId) {
        validateUserId(userId);

        return friendRequestsRepository.findFriendsByUserIdAndStatus(userId, FriendRequestsStatus.ACCEPTED)
                .stream()
                .map(req -> toFriendResponse(req, userId))
                .toList();
    }

    /**
     * 받은 친구 요청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getReceivedRequests(Long userId) {
        validateUserId(userId);

        return friendRequestsRepository.findByToUserUserIdAndStatus(userId, FriendRequestsStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 친구 요청 전송
     */
    @Transactional
    public FriendRequestResponse sendFriendRequest(Long fromUserId, Long toUserId) {
        validateUserId(fromUserId);

        // 자기 자신에게 요청 시: 400 에러
        if (fromUserId.equals(toUserId)) {
            throw new CustomException(ErrorCode.SELF_REQUEST);
        }

        Users fromUser = userRepository.findByIdOrThrow(fromUserId);
        Users toUser = userRepository.findByIdOrThrow(toUserId);

        // 이미 친구이거나 대기 중인 요청이 있을 경우: 409
        boolean alreadyRequested = friendRequestsRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(
                fromUserId, toUserId, FriendRequestsStatus.PENDING);
        boolean alreadyReceived = friendRequestsRepository.existsByToUserUserIdAndFromUserUserIdAndStatus(
                toUserId, fromUserId, FriendRequestsStatus.PENDING);
        boolean alreadyFriend = friendRequestsRepository.existsByFromUserUserIdAndToUserUserIdAndStatus(
                fromUserId, toUserId, FriendRequestsStatus.ACCEPTED) ||
                friendRequestsRepository.existsByToUserUserIdAndFromUserUserIdAndStatus(
                        toUserId, fromUserId, FriendRequestsStatus.ACCEPTED);

        if (alreadyRequested || alreadyReceived || alreadyFriend) {
            throw new CustomException(ErrorCode.ALREADY_FRIEND_OR_REQUESTED);
        }

        // 친구 요청 생성
        FriendRequests friendRequest = new FriendRequests();
        friendRequest.setFromUser(fromUser);
        friendRequest.setToUser(toUser);
        friendRequest.setStatus(FriendRequestsStatus.PENDING);
        friendRequest.setNickname(toUser.getNickname());

        return toResponse(friendRequestsRepository.save(friendRequest));
    }

    /**
     * 친구 요청 수락
     */
    @Transactional
    public FriendRequestResponse acceptFriendRequest(Long userId, Long requestId) {
        validateUserId(userId);

        // 친구 요청을 찾을 수 없는 경우: 404 에러
        FriendRequests friendRequest = friendRequestsRepository
                .findByFriendRequestIdAndToUserUserId(requestId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        // 수락은 '나에게 온 요청(ToUser)'인지 검사해야 합니다!
        if (!friendRequest.getToUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.INVALID_ACCEPT); // INVALID_ACCEPT 에러 사용
        }

        // 대기 중인 상태인지 확인
        if (friendRequest.getStatus() != FriendRequestsStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_ACCEPT);
        }

        friendRequest.setStatus(FriendRequestsStatus.ACCEPTED);

        return toResponse(friendRequestsRepository.save(friendRequest));
    }

    /**
     * 친구 요청 거절
     */
    @Transactional
    public FriendRequestResponse declineFriendRequest(Long userId, Long requestId) {
        validateUserId(userId);

        //친구 요청을 찾을 수 없을 경우 : 404 에러
        FriendRequests friendRequest = friendRequestsRepository
                .findByFriendRequestIdAndToUserUserId(requestId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        // 대기 중인 상태인지 확인
        if (friendRequest.getStatus() != FriendRequestsStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_DECLINED);
        }


        friendRequest.setStatus(FriendRequestsStatus.DECLINED);

        return toResponse(friendRequest);
    }

    /**
     * 친구 요청 취소
     */
    @Transactional
    public FriendRequestResponse cancelFriendRequest(Long userId, Long requestId) {
        validateUserId(userId);

        //404에러
        FriendRequests friendRequest = friendRequestsRepository
                .findByFriendRequestIdAndFromUserUserId(requestId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        // 대기 중인 상태인지 확인
        if (friendRequest.getStatus() != FriendRequestsStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_CANCEL);
        }

        friendRequest.setStatus(FriendRequestsStatus.CANCELED);

        return toResponse(friendRequest);
    }

    /**
     * 친구 삭제
     */
    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        validateUserId(userId);

        FriendRequests friendRequest = friendRequestsRepository
                .findByFromUserUserIdAndToUserUserIdAndStatus(userId, friendId, FriendRequestsStatus.ACCEPTED)
                .or(() -> friendRequestsRepository.findByFromUserUserIdAndToUserUserIdAndStatus(
                        friendId,
                        userId,
                        FriendRequestsStatus.ACCEPTED
                ))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FRIEND_RELATION));

        friendRequestsRepository.delete(friendRequest);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.AUTHENTICATION_ERROR);
        }
    }

    private FriendRequestResponse toResponse(FriendRequests friendRequest) {
        return new FriendRequestResponse(
                friendRequest.getFriendRequestId(),
                friendRequest.getFromUser().getUserId(),
                friendRequest.getToUser().getUserId(),
                friendRequest.getStatus(),
                friendRequest.getCreatedAt(),
                friendRequest.getToUser().getNickname()
        );
    }

    // 친구 목록 조회 시 상대방 기준으로 닉네임 반환
    private FriendRequestResponse toFriendResponse(FriendRequests friendRequest, Long userId) {
        Users opponent = friendRequest.getFromUser().getUserId().equals(userId)
                ? friendRequest.getToUser()
                : friendRequest.getFromUser();
        return new FriendRequestResponse(
                friendRequest.getFriendRequestId(),
                friendRequest.getFromUser().getUserId(),
                friendRequest.getToUser().getUserId(),
                friendRequest.getStatus(),
                friendRequest.getCreatedAt(),
                opponent.getNickname()
        );
    }
}