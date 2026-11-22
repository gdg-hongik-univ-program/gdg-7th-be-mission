package gdg.hongik.mission.dto.request;

import gdg.hongik.mission.entity.Category;

// 상품 등록 요청: { "itemName": "원피스", "price": 20000, "quantity": 10, "category": "의류" }
public record ProductCreateRequest(
        String itemName,
        Long price,
        Long quantity,
        Category category
) {
}
