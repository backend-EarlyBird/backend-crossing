package io.rapa.backendcrossing.items.service;


import io.rapa.backendcrossing.common.constant.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemRepository;
import io.rapa.backendcrossing.items.response.ItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * packageName    : io.rapa.backendcrossing.service
 * fileName       : ItemServiceTests
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : ItemService 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {


    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemsService itemsService;

    @Test
    @DisplayName("아이템 모두 조회 - 성공")
    void findAllItems() {
        // given: DB에 100개가 있더라도 테스트에서는 로직 검증을 위해 2개만 가짜로 만듭니다.
        Items item1 = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Items item2 = Items.builder().itemId(2L).itemName("나무 방패").price(50).build();

        given(itemRepository.findAll()).willReturn(Arrays.asList(item1, item2));

        // when: Service 메서드 실행 (Entity -> DTO 변환)
        List<ItemResponse> result = itemsService.findAllItems();

        // then: 반환된 DTO 결과 검증
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItemName()).isEqualTo("연습용 검");
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("아이템 단건 조회 - 성공")
    void findItemById() {
        // given
        Long targetId = 1L;
        Items item = Items.builder().itemId(targetId).itemName("연습용 검").price(100).build();

        // 💡 Service가 findByIdOrThrow를 호출하므로, 이 메서드가 item을 반환하도록 설정
        given(itemRepository.findByIdOrThrow(targetId)).willReturn(item);

        // when
        ItemResponse result = itemsService.findItemById(targetId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getItemId()).isEqualTo(targetId); // 💡 getItemName() 이었던 오타 수정
        assertThat(result.getItemName()).isEqualTo("연습용 검");
    }

    @Test
    @DisplayName("아이템 단건 조회 - 실패 (CustomException 검증)")
    void findItemById_Fail() {
        // given: 유효하지 않은 ID
        Long invalidId = 999L;

        // 💡 Repository의 findByIdOrThrow가 예외를 던지는 상황을 세팅
        given(itemRepository.findByIdOrThrow(invalidId))
                //()->new CustomException(ErrorCode.USER_NOT_FOUND, "해당 이메일로 사용자 계정을 찾을 수 없습니다.")
                .willThrow(new CustomException(ErrorCode.ITEM_NOT_FOUND));

        // when & then: 예외가 정상적으로 Service 밖으로 던져지는지 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            itemsService.findItemById(invalidId);
        });

        // 💡 우리가 만든 ErrorCode 상수와 일치하는지 검증
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ITEM_NOT_FOUND);
    }
}