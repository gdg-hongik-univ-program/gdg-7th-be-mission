package gdg.hongik.mission.dto;

import java.util.List;

public record CreateOrderResponse(List<OrderedItems> items, int totalPrice) {
}
