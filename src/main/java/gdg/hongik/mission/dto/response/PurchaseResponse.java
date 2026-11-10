package gdg.hongik.mission.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class PurchaseResponse {
    private int totalAmount;
    private List<PurchasedProductResponse> purchasedItems;

    public PurchaseResponse(int totalAmount, List<PurchasedProductResponse> purchasedItems) {
        this.totalAmount = totalAmount;
        this.purchasedItems = purchasedItems;
    }
}