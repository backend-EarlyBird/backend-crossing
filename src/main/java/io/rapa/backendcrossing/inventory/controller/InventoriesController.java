package io.rapa.backendcrossing.inventory.controller;

import io.rapa.backendcrossing.common.annotation.ApiInventoriesSupperts;
import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.inventory.service.InventoriesService;
import io.rapa.backendcrossing.inventory.request.ItemDiscardRequest;
import io.rapa.backendcrossing.inventory.request.ItemPickupRequest;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.rapa.backendcrossing.users.domain.dto.response.AuthLoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName    : io.rapa.backendcrossing.inventory.controller
 * fileName       : InventoriesController
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : Inventories 관련 Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/inventory")
@Tag(name = "Inventories API", description = "인벤토리 관련 API 명세서")
public class InventoriesController implements ApiInventoriesSupperts {

    private final InventoriesService service;


    @ApiGetInventories
    @GetMapping(value = {"", "/", })
    public ResponseEntity<CommonResponse<List<InventoriesResponse>>> getInventories
            (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄

        Long userId = currentUser.getId();

        List<InventoriesResponse> list =service.getInventory(userId);
        return ResponseEntity.ok(CommonResponse.success(list));

    }


    @ApiPickupItem
    @PostMapping("/pickup")
    public ResponseEntity<CommonResponse<InventoriesResponse>> pickupItemAndInventory(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody ItemPickupRequest request) {

        Long userId = currentUser.getId();
        InventoriesResponse result = service.pickupItem(request.getItemId(), request.getQuantity(), userId);

        return ResponseEntity.ok(CommonResponse.successWithMessage(result,
                SuccessMessage.ITEM_PICKUP_SUCCESS.getMessage()));

    }


    @ApiDiscardItem
    @DeleteMapping("{itemId}/discard")
    public ResponseEntity<CommonResponse<InventoriesResponse>> discardItem(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {

        InventoriesResponse result = service.discardItem(itemId, quantity, currentUser.getId());

        return ResponseEntity.ok(CommonResponse.successWithMessage(result,
                SuccessMessage.ITEM_DISCARD_SUCCESS.getMessage()));

        //return ResponseEntity.ok(CommonResponse.success(null));

    }


}
