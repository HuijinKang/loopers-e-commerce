package com.loopers.application.order;

import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderItemDomainService;
import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.product.ProductDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderDomainService orderDomainService;
    private final OrderItemDomainService orderItemDomainService;
    private final ProductDomainService productDomainService;
    private final PointDomainService pointDomainService;

    // TODO: orderItems Dto로 바꿔야함
    public OrderModel createOrder(Long userId, List<OrderItemModel> orderItems, String shippingAddress) {

        OrderModel order = orderDomainService.createOrder(userId, orderItems, shippingAddress);

        for (OrderItemModel orderItem : orderItems) {
            orderItemDomainService.createOrderItem(order, orderItem.getProductId(), orderItem.getOption(), orderItem.getQuantity(), orderItem.calculatePrice());
        }

        productDomainService.deductStock(orderItems);
        pointDomainService.deductPoint(userId, order.getTotalAmount());

        return order;
    }
}
