package gdg.hongik.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateOrderResponse(
        @JsonProperty("items") List<OrderedItems> purchasedProducts,
        int totalPrice
) {
}
