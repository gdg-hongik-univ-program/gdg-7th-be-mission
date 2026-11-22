package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gdg.hongik.mission.entity.Category;

public record DeleteProductResponse(
        Long id,
        @JsonProperty("itemName") String name,
        Category category,
        int price,
        @JsonProperty("quantity") int stock
) {
}
