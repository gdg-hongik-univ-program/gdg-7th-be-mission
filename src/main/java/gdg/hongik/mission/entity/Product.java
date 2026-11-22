package gdg.hongik.mission.entity;

import gdg.hongik.mission.exception.BusinessException;
import gdg.hongik.mission.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private int price;
    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public Product(String name, int price, int stockQuantity, Category category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "추가할 재고 수량은 1개 이상이어야 합니다.");
        }
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int count) {
        if (count <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "구매 수량은 1개 이상이어야 합니다.");
        }
        if (this.stockQuantity < count) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK, this.name + " 상품의 재고가 부족합니다.");
        }
        this.stockQuantity -= count;
    }

}