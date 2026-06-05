package io.rapa.backendcrossing.users.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.MeDetailResponse;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.MemberSubstitution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerSupporter{

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<UserCreateResponse>> registerUser(
            @Valid  @RequestBody UserCreateRequest request
    ){
        UserCreateResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        CommonResponse.successWithMessage(
                                response,
                                SuccessMessage.USER_CREATE_SUCCESS.getMessage()
                        )
                );
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<MeDetailResponse>> geMeDetails(
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        log.info("1");
        MeDetailResponse meDetails = userService.getDetailofMe(currentUser.getEmail());
        log.info("3");
        return ResponseEntity.ok()
                .body(
                        CommonResponse.successWithMessage(
                                meDetails,
                                null
                        )
                );
    }

}
