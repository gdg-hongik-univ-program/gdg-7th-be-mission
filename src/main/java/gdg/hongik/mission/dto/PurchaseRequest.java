package gdg.hongik.mission.dto;

import java.util.List;

public record PurchaseRequest(List<PurchaseItemRequest> items) {

    public record PurchaseItemRequest(Long productId, Long quantity) {
    }
}