package io.rapa.backendcrossing.friendRequests.controller;

import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.friendRequests.constants.FriendRequestsStatus;
import io.rapa.backendcrossing.friendRequests.entity.FriendRequests;
import io.rapa.backendcrossing.friendRequests.reponse.FriendRequestResponse;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.friendRequests.service.FriendRequestsService;
import io.rapa.backendcrossing.inventory.controller.InventoriesController;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : io.rapa.backendcrossing.friendRequests.controller
 * fileName       : FriendRequestsControllerTest
 * author         : Admin
 * date           : 26. 6. 4.
 * description    : FriendRequestsController 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 4.        Admin       최초 생성
 */

@WebMvcTest(
        controllers = FriendRequestsController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@DisplayName("FriendRequestsController 단위 테스트")
public class FriendRequestsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendRequestsService friendRequestsService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TokenService tokenService;


    private Users userA;
    private Users userB;
    private final Long userId = 1L;
    private final Long requestId = 2L;
    @Autowired
    private FriendRequestsRepository friendRequestsRepository;

    @Test
    @DisplayName("친구 목록 조회 성공")
    void will_getFriends() throws Exception {
        // given: 서비스의 동작을 정의 (Repository는 여기서 관여하지 않음)
        FriendRequestResponse response = new FriendRequestResponse();
        response.setStatus(FriendRequestsStatus.ACCEPTED);

        given(friendRequestsService.getFriends(anyLong())).willReturn(List.of(response));

        // when & then: 컨트롤러 엔드포인트를 호출
        mockMvc.perform(get("/api/friend-requests/friends/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}