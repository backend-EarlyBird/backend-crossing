package io.rapa.backendcrossing.profiles.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.profiles.domain.dto.ProfileDetailResponse;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface ProfileControllerSupporter {

    ResponseEntity<CommonResponse<ProfileDetailResponse>> getProfile(CurrentUser currentUser);
}
