package gdg.hongik.mission.dto.response;

// 구매한 상품 한 줄. price = 1개 가격 x 구매 수량
public record PurchasedProductResponse(
        String itemName,
        long count,
        long price
) {
}
