package io.rapa.backendcrossing.items.Controller;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.controller.ItemsController;
import io.rapa.backendcrossing.items.response.ItemsResponse;
import io.rapa.backendcrossing.items.service.ItemsService;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.Controller
 * fileName       : ItemsControllerTests
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : ItemsController 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@WebMvcTest(ItemsController.class)
@DisplayName("ItemsController 단위 테스트")
@Slf4j
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 검문소 통과
public class ItemsControllerTests {

    @Autowired
    private MockMvc mockMvc; //가짜 요청

    @MockitoBean
    private ItemsService itemsService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserService userService;



    @Test
    //@WithMockUser //로그인된 사용자처럼 보이게하기
    @DisplayName("아이템 모두 조회 - /api/v1/items")
    void readAllItems() throws Exception {
        //given
        given(itemsService.findAllItems()).willReturn(Arrays.asList(
                ItemsResponse.builder().itemName("연습용 검").build(),
                ItemsResponse.builder().itemName("나무 방패").build()
        ));

        //when / then

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true)) // 공통 응답 성공 확인
                .andExpect(jsonPath("$.data.length()").value(2)) // 💡 핵심: data 리스트의 길이 확인
                .andExpect(jsonPath("$.data[0].itemName").value("연습용 검"));
    }



    @Test
    @DisplayName("아이템 아이디로 조회 - /api/v1/items/{id}")
    void readItemById() throws Exception {
        // given
        given(itemsService.findItemById(3L))
                .willReturn(ItemsResponse.builder().itemName("테스트 아이템").build());

        // when & then
        mockMvc.perform(get("/api/v1/items/3")
                        .contentType(MediaType.APPLICATION_JSON))
                // then: 검증
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true)) // 공통 응답 성공 확인
                .andExpect(jsonPath("$.data.itemName").value("테스트 아이템"));


    }

    @Test
    @DisplayName("아이템 아이디로 조회(실패(404)) - /api/v1/items/{id}")
    void findItemById_Fail() throws Exception {
        //given
        Long invalidId = 999L;

        //when / then
        // 서비스가 에러를 던지도록 설정
        given(itemsService.findItemById(invalidId))
                .willThrow(new CustomException(ErrorCode.ITEM_NOT_FOUND));

        mockMvc.perform(get("/api/v1/items/" + invalidId))
                .andExpect(status().isNotFound()) // 404 상태코드 확인
                .andExpect(jsonPath("$.success").value(false)) // success: false 확인
                .andExpect(jsonPath("$.message").value(ErrorCode.ITEM_NOT_FOUND.getDescription())); // 메시지 확인
    }

}
