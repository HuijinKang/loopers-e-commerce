package com.loopers.domain.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.domain.brand.BrandModel;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.coupon.*;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserModel;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderFacadeIntegrationTest {

    @Autowired private OrderFacade orderFacade;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PointDomainService pointDomainService;
    @Autowired private PointRepository pointRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private IssuedCouponRepository issuedCouponRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() { databaseCleanUp.truncateAllTables(); }

    @DisplayName("주문 생성 플로우")
    @Nested
    class CreateOrderFlow {

        @Test
        @DisplayName("성공 시 주문/아이템 저장, 재고 차감, 포인트 차감이 이루어진다")
        void succeeds_and_persists_all() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("of-int-success@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel p1 = productRepository.save(ProductModel.of(brand.getId(), "상품1", 5000L, 5, ProductStatus.ON_SALE));
            ProductModel p2 = productRepository.save(ProductModel.of(brand.getId(), "상품2", 3000L, 3, ProductStatus.ON_SALE));
            pointDomainService.chargePoint(user, 10_000L);

            OrderV1Dto.CreateOrderCommand command = new OrderV1Dto.CreateOrderCommand(
                    user.getId(),
                    "ORD-IT-" + System.currentTimeMillis(),
                    "서울시",
                    null,
                    1_000L,
                    List.of(
                            new OrderV1Dto.CreateOrderCommand.OrderItemCommand(p1.getId(), Option.of("RED","M"), 1, 5000L),
                            new OrderV1Dto.CreateOrderCommand.OrderItemCommand(p2.getId(), Option.of("BLK","L"), 2, 3000L)
                    )
            );

            // act
            Long orderId = orderFacade.placeOrder(command);

            // assert
            assertThat(orderId).isNotNull();
            assertThat(orderRepository.findById(orderId)).isPresent();

            ProductModel rp1 = productRepository.findById(p1.getId()).orElseThrow();
            ProductModel rp2 = productRepository.findById(p2.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(rp1.getStock()).isEqualTo(4),
                    () -> assertThat(rp2.getStock()).isEqualTo(1)
            );

            long finalPoint = pointRepository.findByUserId(user.getId()).orElseThrow().getAmount();
            assertThat(finalPoint).isEqualTo(9_000L);
        }

        @Test
        @DisplayName("포인트 부족으로 실패하면 전체가 롤백된다(주문/재고/쿠폰 상태)")
        void rolls_back_all_when_point_insufficient() {
            // arrange
            UserModel user = userJpaRepository.save(UserModel.of("of-int-rollback@test.com", "주문자", Gender.MALE, "1990-01-01"));
            BrandModel brand = brandRepository.save(BrandModel.of("브랜드"));
            ProductModel p1 = productRepository.save(ProductModel.of(brand.getId(), "상품1", 5000L, 2, ProductStatus.ON_SALE));
            pointDomainService.chargePoint(user, 1_000L); // 일부만 충전

            CouponModel coupon = couponRepository.save(CouponModel.ofFixed("FIX-1000-RB", "fixed", 1000L));
            IssuedCouponModel issued = issuedCouponRepository.save(IssuedCouponModel.of(coupon, user.getId(), IssuedCouponStatus.ISSUED));

            int initialStock = p1.getStock();
            IssuedCouponStatus initialStatus = issued.getStatus();
            long initialPoint = pointRepository.findByUserId(user.getId()).orElseThrow().getAmount();

            OrderV1Dto.CreateOrderCommand command = new OrderV1Dto.CreateOrderCommand(
                    user.getId(),
                    "ORD-RB-" + System.currentTimeMillis(),
                    "서울시",
                    issued.getId(), // 쿠폰 먼저 사용 시도
                    5_000L, // 보유보다 큰 사용 포인트로 실패 유도
                    List.of(new OrderV1Dto.CreateOrderCommand.OrderItemCommand(p1.getId(), Option.of("RED","M"), 1, 5000L))
            );

            // act
            assertThrows(CoreException.class, () -> orderFacade.placeOrder(command));

            // assert - 모든 상태 롤백
            ProductModel rp1 = productRepository.findById(p1.getId()).orElseThrow();
            IssuedCouponModel reloadedCoupon = issuedCouponRepository.findById(issued.getId()).orElseThrow();
            long finalPoint = pointRepository.findByUserId(user.getId()).orElseThrow().getAmount();

            assertAll(
                    () -> assertThat(orderRepository.countByUserId(user.getId())).isEqualTo(0),
                    () -> assertThat(rp1.getStock()).isEqualTo(initialStock),
                    () -> assertThat(reloadedCoupon.getStatus()).isEqualTo(initialStatus),
                    () -> assertThat(finalPoint).isEqualTo(initialPoint)
            );
        }
    }
}
