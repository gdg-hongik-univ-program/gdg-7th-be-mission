package gdg.hongik.mission.dto.response;

import lombok.Getter;

@Getter
public class ProductResponse {
    private final Long productId;
    private final String name;
    private final int price;
    private final int stockQuantity;

    public ProductResponse(Long productId, String name, int price, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}