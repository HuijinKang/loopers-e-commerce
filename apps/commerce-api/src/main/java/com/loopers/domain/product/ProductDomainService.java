package com.loopers.domain.product;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDomainService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductModel getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<ProductModel> getProducts(int page, int size, ProductSortType sortType, ProductStatus status) {
        return productRepository.search(page, size, sortType, status);
    }

    @Transactional
    public void deductStock(List<OrderItemModel> orderItems) {
        for (OrderItemModel item : orderItems) {
            ProductModel product = productRepository.findByIdForUpdate(item.getProductId())
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품이 존재하지 않습니다."));

            if (product.getStock() < item.getQuantity()) {
                throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다. 상품: " + product.getName());
            }

            product.decreaseStock(item.getQuantity());
            productRepository.save(product);
        }
    }
}
