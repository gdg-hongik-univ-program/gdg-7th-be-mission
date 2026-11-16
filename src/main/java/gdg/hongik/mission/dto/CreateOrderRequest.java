package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateOrderRequest(
        Long productId,
        @JsonProperty("count") int quantity
) {
}
