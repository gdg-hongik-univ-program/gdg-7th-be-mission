package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gdg.hongik.mission.entity.Category;

public record ProductRequest(@JsonProperty("itemName") String name, int price, @JsonProperty("quantity") int stockQuantity, Category category
) {
}