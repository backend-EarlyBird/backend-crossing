package io.rapa.backendcrossing.friendRequests.reponse;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    Long friendRequestId;
    Long fromUserId;
    Long toUserId;
    FriendRequestsStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt;
    String nickname;

}
