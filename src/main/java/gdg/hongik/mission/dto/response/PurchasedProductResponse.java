package gdg.hongik.mission.dto.response;

public record PurchasedProductResponse(
        String productName,
        int quantity,
        int subtotalAmount) {
}