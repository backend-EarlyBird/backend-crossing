package io.rapa.backendcrossing.friendRequests.reponse;

import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.users.domain.entity.Users;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.dto
 * fileName       : FriendRequestResponse
 * author         : Admin
 * date           : 26. 6. 4.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {
    private Long friendRequestId;
    private Users fromUser;
    private Users toUser;
    private FriendRequestsStatus status;
    private LocalDateTime createdAt;
}
