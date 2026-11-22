package gdg.hongik.mission.dto.request;


// 장바구니 구매 요청의 상품 한 줄: { "id": 1, "count": 2 }
public record PurchaseItemRequest(
        Long id,
        Long count
) {
}
