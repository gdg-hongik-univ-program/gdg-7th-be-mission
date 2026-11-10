package gdg.hongik.mission.dto.response;

import lombok.Getter;

@Getter
public class PurchasedProductResponse {
    private final String productName;
    private final int quantity;
    private final int subtotalAmount;

    public PurchasedProductResponse(String productName, int quantity, int subtotalAmount) {
        this.productName = productName;
        this.quantity = quantity;
        this.subtotalAmount = subtotalAmount;
    }
}