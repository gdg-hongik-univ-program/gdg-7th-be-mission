package gdg.hongik.mission.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StockUpdateRequest(
        @NotBlank(message = "상품명은 필수입니다.") String itemName,
        @Positive(message = "추가할 재고 수량은 1개 이상이어야 합니다.") int quantity) {
}
