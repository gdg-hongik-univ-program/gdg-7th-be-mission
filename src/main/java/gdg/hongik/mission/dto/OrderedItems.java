package gdg.hongik.mission.dto;

public record OrderedItems(
        Long productId,
        String productName,
        int quantity,
        int subtotal,
        int remainingStock
) {
}
