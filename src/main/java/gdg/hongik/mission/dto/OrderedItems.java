package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderedItems(
        Long productId,
        @JsonProperty("itemName") String productName,
        @JsonProperty("count") int quantity,
        int subtotal,
        @JsonProperty("quantity") int remainingStock
) {
}
