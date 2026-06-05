package io.rapa.backendcrossing.profiles.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.profiles.domain.dto.ProfileDetailResponse;
import io.rapa.backendcrossing.profiles.service.ProfileService;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping( "/api/v1/users/me")
public class ProfileController implements ProfileControllerSupporter {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<ProfileDetailResponse>> getProfile(
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        return ResponseEntity.ok().body(
                CommonResponse.successWithMessage(
                        profileService.getDetail(currentUser.getId()),
                        null
                )
        );
    }
}
