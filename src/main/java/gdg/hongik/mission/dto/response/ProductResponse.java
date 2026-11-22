package gdg.hongik.mission.dto.response;

import gdg.hongik.mission.entity.Category;
import gdg.hongik.mission.entity.Product;

public record ProductResponse(
                Long id,
                String itemName,
                int price,
                int quantity,
                Category category) {

        public static ProductResponse from(Product product) {
                return new ProductResponse(
                                product.getProductId(),
                                product.getName(),
                                product.getPrice(),
                                product.getStockQuantity(),
                                product.getCategory());
        }
}