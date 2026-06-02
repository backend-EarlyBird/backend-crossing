package io.rapa.backendcrossing.npcs.controller;

import io.rapa.backendcrossing.common.annotation.ApiNpcsSupperts;
import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.items.response.ItemsResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : io.rapa.backendcrossing.controller
 * fileName       : ItemsController
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : Npc 관련 Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/npcs")
@Tag(name = "Npc API", description = "Npc 관련 API 명세서")
public class NpcController implements ApiNpcsSupperts {


    /*//npc 목록조회(전체)
    @GetMapping("/")
    public ResponseEntity<CommonResponse<List<ItemsResponse>>> findAllNpcs() {
        return ResponseEntity.ok(CommonResponse.builder().build());
    }

    //npc 목록조회(단일)
    @GetMapping("/{npcId}")
    public ResponseEntity<CommonResponse<List<ItemsResponse>>> getNpcInfo(
            @Parameter(description = "Npc ID") @PathVariable("npcId") Long npcId
    ) {
        return ResponseEntity.ok(CommonResponse.builder().build());
    }*/

}