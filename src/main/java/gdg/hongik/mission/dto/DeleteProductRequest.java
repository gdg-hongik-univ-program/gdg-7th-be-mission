package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteProductRequest(
        @JsonProperty("itemName") String name
) {
}
