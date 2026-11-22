package gdg.hongik.mission.dto.request;

// 재고 수량 변경 요청의 상품 한 줄: { "itemName": "원피스", "quantity": 10 }
public record StockUpdateRequest(
        String itemName,
        Long quantity
) {
}