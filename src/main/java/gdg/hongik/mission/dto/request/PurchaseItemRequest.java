package gdg.hongik.mission.dto.request;

public record PurchaseItemRequest(
        Long productId,
        int quantity) {
}