package gdg.hongik.mission.dto.request;

public record ProductCreateRequest(
        String name,
        int price,
        int stockQuantity) {
}