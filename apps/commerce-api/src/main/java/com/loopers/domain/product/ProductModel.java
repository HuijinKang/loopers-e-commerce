package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.BrandModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandModel brand;
    private String name;
    private Long price;
    private int stock;
    private int likeCount;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    public ProductModel(BrandModel brand, String name, Long price, int stock) {
        if (price < 0) throw new CoreException(ErrorType.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
        if (stock < 0) throw new CoreException(ErrorType.BAD_REQUEST, "재고는 0 이상이어야 합니다.");
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = ProductStatus.ON_SALE;
        this.likeCount = 0;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) throw new CoreException(ErrorType.BAD_REQUEST, "감소 수량은 0보다 커야 합니다.");
        if (stock < quantity) throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.");
        this.stock -= quantity;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public boolean isAvailable() {
        return this.status == ProductStatus.ON_SALE && this.stock > 0;
    }
}


