package com.loopers.application.order;

import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.user.UserDomainService;
import com.loopers.domain.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderQueryFacade {

    private final OrderDomainService orderDomainService;
    private final UserDomainService userDomainService;

    @Transactional(readOnly = true)
    public OrderInfo getOrder(String email, Long orderId) {
        UserModel user = userDomainService.getUser(email);
        OrderModel order = orderDomainService.getOrder(orderId);
        if (!order.getUserId().equals(user.getId())) {
            throw new com.loopers.support.error.CoreException(com.loopers.support.error.ErrorType.FORBIDDEN, "본인 주문만 조회할 수 있습니다.");
        }
        return OrderInfo.of(order, orderDomainService.getOrderItems(orderId));
    }

    @Transactional(readOnly = true)
    public List<OrderInfo> getOrders(String email) {
        UserModel user = userDomainService.getUser(email);
        return orderDomainService.getUserOrders(user.getId()).stream()
                .map(o -> OrderInfo.of(o, orderDomainService.getOrderItems(o.getId())))
                .toList();
    }
}
