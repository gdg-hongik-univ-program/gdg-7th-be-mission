package gdg.hongik.mission.dto.request;


// 상품 삭제 요청: { "itemName": "원피스" }
public record ProductDeleteRequest(
        String itemName
) {
}
