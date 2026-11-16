package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddStockRequest(
        @JsonProperty("itemName") String name,
        int quantity
) {
}
