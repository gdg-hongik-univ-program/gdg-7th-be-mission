package gdg.hongik.mission.dto.request;

import gdg.hongik.mission.entity.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
        @NotBlank(message = "상품명은 필수입니다.") String itemName,
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.") int price,
        @Min(value = 0, message = "재고는 0개 이상이어야 합니다.") int quantity,
        @NotNull(message = "카테고리는 필수입니다.") Category category) {
}
