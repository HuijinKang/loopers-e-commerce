package com.loopers.domain.order;

import java.util.List;

public interface OrderItemRepository {
    void save(OrderItemModel orderItem);
    List<OrderItemModel> saveAll(List<OrderItemModel> orderItems);
}
