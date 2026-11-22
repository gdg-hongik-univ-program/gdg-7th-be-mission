package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AddStockRequest(
        @JsonProperty("itemName")
        @NotBlank(message = "상품명은 비어 있을 수 없습니다.")
        String name,

        @Positive(message = "추가 수량은 1 이상이어야 합니다.")
        int quantity
) {
}
