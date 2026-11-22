package gdg.hongik.mission.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseItemRequest(
        @NotNull(message = "상품 ID는 필수입니다.") Long id,
        @Positive(message = "구매 수량은 1개 이상이어야 합니다.") int count) {
}
