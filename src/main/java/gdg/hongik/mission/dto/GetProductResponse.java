package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetProductResponse(
        Long id,
        @JsonProperty("itemName") String name,
        int price,
        @JsonProperty("quantity") int stock
) {
}
