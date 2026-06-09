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
import io.rapa.backendcrossing.users.domain.dto.request.UserCreateRequest;
import io.rapa.backendcrossing.users.domain.dto.response.UserCreateResponse;
import io.rapa.backendcrossing.users.domain.entity.Users;
import io.rapa.backendcrossing.users.repository.UserRepository;
import io.rapa.backendcrossing.wallets.domain.entity.Wallets;
import io.rapa.backendcrossing.wallets.repository.WalletRepository;
import io.rapa.backendcrossing.users.service.UserService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
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
    @Autowired
    WalletRepository walletRepository;

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    List<UserCreateResponse> userResponses = new ArrayList<>();

    Long npcId;
    Long npcItemId;
    @Autowired
    private UserService userService;


    @Nested
    @DisplayName("Describe : NpcShopService의 purchase() 메서드의 동시성 제어 테스트")
    class Describe_concurrency_test {


        @Nested
        @DisplayName("Context : 100명의 유저가 동시 접속하는 경우")
        class Context_with_100_users {

            @BeforeEach
            void setUp() {
                for (int i = 0; i < 100; i++) {
                    userResponses.add(
                            userService.registerUser(
                                    new UserCreateRequest(
                                            "테스트사용자%d@naver.com".formatted(i),
                                            passwordEncoder.encode("wjdtn3902"),
                                            "힙합은했지만 랩은 안했어요"
                                    )
                            )
                    );
                }
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
                        NpcItems.builder().npc(npc).item(item).quantity(100).sortOrder(1).build()
                );
                npcItemId = npcItem.getNpcItemId();
            }

            @Test
            @DisplayName("It : 락이 적용되지 않은 경우 : NPC 아이템 구매 시 100명의 유저로 동시 구매하는 경우 해당 NPC상점의 상품은 무결하지 못한다.")
            void It_락이_설정되지_않은_경우() throws InterruptedException {
                int ITER = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(100);
                CountDownLatch countDownLatch = new CountDownLatch(100);
                NpcItems foundedItem = npcItemsRepository.findByIdOrThrow(npcItemId);
                log.info(foundedItem.getQuantity() + "");
                for (int i = 0; i < ITER; i++) {
                    final int finalI = i;
                    executorService.execute(() -> {
                        try {
                            UserCreateResponse response = userResponses.get(finalI);
                            npcShopService.purchase(
                                    response.userId(),
                                    npcId,
                                    npcItemId,
                                    new NpcPurchaseRequest(1)
                            );
                            successCount.incrementAndGet();
                        } catch (RuntimeException e) {
                            log.info("구매실패" + e.getMessage());
                            failureCount.incrementAndGet();
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
                }
                countDownLatch.await();
                executorService.shutdown();
                foundedItem = npcItemsRepository.findByIdOrThrow(npcItemId);
                log.info("락이 적용되지 않은 경우의 NPC 상품 구매");
                log.info("성공한 주문수 : %d / 실패한 주문수 : %d / NPC 상품 남은 재고 수 : %d".formatted(successCount.get(), failureCount.get(), foundedItem.getQuantity()));
                Assertions.assertThat(foundedItem.getQuantity()).isNotEqualTo(0);
            }

//            @Test
//            @DisplayName("It : NPC 아이템 구매 시 100명의 유저로 동시 구매하는 경우 해당 NPC상점의 상품은 무결해야한다.")
//            void It_락이_설정된_경우() throws InterruptedException {
//                int ITER = 100;
//                ExecutorService executorService = Executors.newFixedThreadPool(100);
//                CountDownLatch countDownLatch = new CountDownLatch(100);
//                NpcItems foundedItem = npcItemsRepository.findByIdOrThrow(npcItemId);
//                log.info(foundedItem.getQuantity() + "");
//                for (int i = 0; i < ITER; i++) {
//                    final int finalI = i;
//                    executorService.execute(() -> {
//                        try {
//                            UserCreateResponse response = userResponses.get(finalI);
//                            npcShopService.purchaseWithLock(
//                                    response.userId(),
//                                    npcId,
//                                    npcItemId,
//                                    new NpcPurchaseRequest(1)
//                            );
//                            successCount.incrementAndGet();
//                        } catch (RuntimeException e) {
//                            log.info("구매실패" + e.getMessage());
//                            failureCount.incrementAndGet();
//                        } finally {
//                            countDownLatch.countDown();
//                        }
//                    });
//                }
//                countDownLatch.await();
//                executorService.shutdown();
//                foundedItem = npcItemsRepository.findByIdOrThrow(npcItemId);
//                log.info("락이 적용된 경우의 NPC 상품 구매");
//                log.info("성공한 주문수 : %d / 실패한 주문수 : %d / 남은 재고 수 : %d".formatted(successCount.get(), failureCount.get(), foundedItem.getQuantity()));
//                Assertions.assertThat(foundedItem.getQuantity()).isEqualTo(0);
//            }
        }
    }
}
