package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record DeleteProductRequest(
        @JsonProperty("itemName")
        @NotBlank(message = "상품명은 비어 있을 수 없습니다.")
        String name
) {
}
