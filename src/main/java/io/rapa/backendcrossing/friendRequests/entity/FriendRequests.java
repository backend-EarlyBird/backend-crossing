package io.rapa.backendcrossing.friendRequests.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.users.domain.entity.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.catalina.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * packageName    : io.rapa.backendcrossing.friends.entity
 * fileName       : FriendRequests
 * author         : Admin
 * date           : 26. 6. 4.
 * description    :  FriendRequests Domain Entity
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */
@Data
@Entity
@Table(name = "friend_requests")
public class FriendRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendRequestId;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private Users fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private Users toUser; // Changed here

    @Enumerated(EnumType.STRING)
    private FriendRequestsStatus status;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(nullable = false) // This forces the "cannot be null" constraint
    private String nickname;
}
