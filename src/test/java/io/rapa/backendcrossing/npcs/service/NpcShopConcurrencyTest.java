package io.rapa.backendcrossing.npcs.service;

import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.npcs.entity.NpcItems;
import io.rapa.backendcrossing.npcs.entity.Npcs;
import io.rapa.backendcrossing.npcs.repository.NpcItemsRepository;
import io.rapa.backendcrossing.npcs.repository.NpcsRepository;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootTest
@Slf4j
public class NpcShopConcurrencyTest {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NpcShopService npcShopService;
    @Autowired
    NpcItemsRepository npcItemsRepository;
    @Autowired
    NpcsRepository npcsRepository;
    @Autowired
    ItemsRepository itemsRepository;

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    List<Users> users;

    Long npcId;
    Long npcItemId;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            users.add(
                    UserUtils.makeUsers(
                            "사용자%d@naver.com".formatted(i),
                            passwordEncoder.encode("wjdtn3902")
                    )
            );
        }
        userRepository.saveAll(users);

        Items item = itemsRepository.save(Items.builder()
                .rId("potion_hp_001").itemName("HP 포션")
                .itemType(ItemType.CONSUMABLE).itemGrade(ItemGrade.COMMON)
                .description("HP를 50 회복합니다.").price(30).sellPrice(10)
                .build());

        Npcs npc = npcsRepository.save(
                Npcs.builder().rId("npc_merchant").name("상인").locationKey("market").active(true).build()
        );
        npcId = npc.getNpcId();

        NpcItems npcItem = npcItemsRepository.save(
                NpcItems.builder().npc(npc).item(item).quantity(99).sortOrder(1).build()
        );
        npcItemId = npcItem.getNpcItemId();
    }

    @Nested
    @DisplayName("Describe : NpcShopService의 purchase() 메서드의 동시성 제어 테스트")
    class Describe_concurrency_test {

        @Nested
        @DisplayName("Context : 100명의 유저가 동시 접속하는 경우")
        class Context_with_100_users {

            @Test
            @DisplayName("It : NPC 아이템 구매 시 99명의 유저로 동시 구매하는 경우 해당 NPC상점의 상품은 무결해야한다.")
            void It_NPC상점_1번_물건_100번_구매_시_남은_재고_0개() throws InterruptedException {
                int ITER = 99;
                ExecutorService executorService = Executors.newFixedThreadPool(99);
                CountDownLatch countDownLatch = new CountDownLatch(99);
                for (int i = 0; i < ITER; i++) {
                    final int finalI = i;
                    executorService.execute(() -> {
                        try {
                            Users currentUser = users.get(finalI);
                            npcShopService.purchase(
                                    currentUser.getUserId(),
                                    npcId,
                                    npcItemId,
                                    new NpcPurchaseRequest(1)
                            );
                            successCount.incrementAndGet();
                        } catch (RuntimeException e) {
                            log.info(e.getMessage());
                            failureCount.incrementAndGet();
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
                }
                countDownLatch.await();
                executorService.shutdown();
                NpcItems foundedItem = npcItemsRepository.findByIdOrThrow(1L);
                log.info(foundedItem.getQuantity() + "");
                log.info("성공한 주문수 : %d \n 실패한 주문수 : %d \n 남은 재고 수 : %d".formatted(successCount.get(), failureCount.get(), foundedItem.getQuantity()));
                Assertions.assertThat(foundedItem.getQuantity()).isNotEqualTo(0);
            }
        }
    }
}
