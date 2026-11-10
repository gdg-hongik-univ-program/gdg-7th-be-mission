package gdg.hongik.mission.dto.response;

import java.util.List;

public record PurchaseResponse(
        int totalAmount,
        List<PurchasedProductResponse> purchasedItems) {
}