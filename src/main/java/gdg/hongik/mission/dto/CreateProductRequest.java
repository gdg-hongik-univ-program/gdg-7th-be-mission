package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gdg.hongik.mission.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(
        @JsonProperty("itemName")
        @NotBlank(message = "상품명은 비어 있을 수 없습니다.")
        String name,

        @NotNull(message = "카테고리는 필수입니다.")
        Category category,

        @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
        int price,

        @JsonProperty("quantity")
        @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
        int stock
) {
}
