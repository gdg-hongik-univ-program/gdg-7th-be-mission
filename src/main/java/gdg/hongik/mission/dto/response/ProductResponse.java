package gdg.hongik.mission.dto.response;

import gdg.hongik.mission.entity.Category;
import gdg.hongik.mission.entity.Product;

// 상품 정보 응답 (조회/등록/재고 추가/삭제 후 목록에서 공통 사용)
public record ProductResponse(
        Long id,
        String itemName,
        long price,
        long quantity,
        Category category
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getItemName(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory()
        );
    }
}

//long을 사용한 이유