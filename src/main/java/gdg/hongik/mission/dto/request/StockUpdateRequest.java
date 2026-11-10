package gdg.hongik.mission.dto.request;

public record StockUpdateRequest(
        Long productId,
        int quantityToAdd) {
}