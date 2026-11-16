package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateProductResponse(
        Long id,
        @JsonProperty("itemName") String name,
        int price,
        @JsonProperty("quantity") int stock
) {
}
