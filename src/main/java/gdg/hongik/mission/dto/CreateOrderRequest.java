package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        @Positive(message = "상품 ID는 1 이상이어야 합니다.")
        Long productId,
        @JsonProperty("count")
        @Positive(message = "구매 수량은 1 이상이어야 합니다.")
        int quantity
) {
}
