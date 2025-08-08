package com.loopers.domain.order;

import com.loopers.interfaces.api.order.OrderV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemDomainService {

    private final OrderItemRepository orderItemRepository;

    @Transactional
    public List<OrderItemModel> createItems(OrderModel order, List<OrderV1Dto.CreateOrderCommand.OrderItemCommand> items) {
        List<OrderItemModel> orderItems = items.stream()
                .map(i -> OrderItemModel.of(
                        order,
                        i.productId(),
                        i.option(),
                        i.quantity(),
                        i.price()
                ))
                .toList();

        return orderItemRepository.saveAll(orderItems);
    }
}
