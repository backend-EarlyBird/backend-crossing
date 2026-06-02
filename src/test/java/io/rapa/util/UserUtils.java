package io.rapa.util;

import io.rapa.backendcrossing.users.domain.dto.request.AuthLoginRequest;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;

public class UserUtils {
    public static Users makeUsers(String email, String password){
        return Users.builder()
                .email(email)
                .password(password)
                .nickName("닉네임")
                .build();
    }

    public static AuthLoginRequest makeLoginRequest(String email, String password){
        return new AuthLoginRequest(
                email,
                password
        );
    }

    public static UserCreateRequest makeCreateRequest(String email){
        return new UserCreateRequest(
                email,
                "wjd747",
                "KF16"
        );
    }
}
