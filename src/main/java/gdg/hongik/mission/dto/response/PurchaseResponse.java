package gdg.hongik.mission.dto.response;

import java.util.List;

// 구매 응답
public record PurchaseResponse(
        long totalPrice,
        List<PurchasedProductResponse> items
) {
}