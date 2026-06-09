package io.rapa.util;

import io.rapa.backendcrossing.auth.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;

public class UserUtils {
    public static Users makeUsers(String email, String password){
        return Users.builder()
                .email(email)
                .password(password)
                .nickname("닉네임")
                .build();
    }

    public static AuthLoginRequest makeLoginRequest(String email, String password){
        return new AuthLoginRequest(
                email,
                password
        );
    }

    public static UserCreateRequest makeCreateRequest(String email, String password){
        return new UserCreateRequest(
                email,
                password,
                "KF16"
        );
    }
    public static FriendRequests buildRequest(Users from, Users to, FriendRequestsStatus status) {
        FriendRequests request = new FriendRequests();
        request.setFromUser(from);
        request.setToUser(to);
        request.setStatus(status);
        request.setNickname(to.getNickname());
        return request;
    }
}
