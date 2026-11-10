package gdg.hongik.mission.dto.response;

public record ProductResponse(
        Long productId,
        String name,
        int price,
        int stockQuantity) {

}