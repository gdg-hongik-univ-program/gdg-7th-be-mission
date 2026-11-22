package gdg.hongik.mission.dto.response;

import java.util.List;

public record PurchaseResponse(
                int totalPrice,
                List<PurchasedProductResponse> items) {
}