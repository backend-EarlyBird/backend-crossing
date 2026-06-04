package io.rapa.backendcrossing.friendRequests.controller;

import io.rapa.backendcrossing.friendRequests.reponse.FriendRequestResponse;
import io.rapa.backendcrossing.friendRequests.repository.FriendRequestsRepository;
import io.rapa.backendcrossing.friendRequests.service.FriendRequestsService;
import io.rapa.backendcrossing.inventory.controller.InventoriesController;
import io.rapa.backendcrossing.security.service.TokenService;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
@WebMvcTest(FriendRequestsControllerTests.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("FriendRequestsController 단위 테스트")
@Slf4j
public class FriendRequestsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FriendRequestsService friendRequestsService;

    @MockitoBean
    private UserService userService;



    @MockitoBean
    private TokenService tokenService;


    @Test
    @WithMockUser
    @DisplayName("친구 목록 조회 API 테스트")
    void getFriends_Success() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }






}
