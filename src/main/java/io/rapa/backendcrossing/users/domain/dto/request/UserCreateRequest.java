package io.rapa.backendcrossing.users.domain.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @Pattern(regexp="^[0-9A-Za-z._%+-]+@[0-9A-Za-z.-]+\\.[A-Za-z]{2,6}$") String email,
        @Size(min = 8, max = 64, message = "비밀번호는 8 ~ 64자 사이로 입력해주세요.") String password,
        @Size(min = 2, max = 20, message = "닉네임은 2 ~ 20자 사이로 입력해주세요.") String nickname
) {
}
