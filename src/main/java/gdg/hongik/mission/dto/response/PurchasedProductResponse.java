package gdg.hongik.mission.dto.response;

import gdg.hongik.mission.entity.Product;

public record PurchasedProductResponse(
        String itemName,
        int count,
        int price) {

    public static PurchasedProductResponse of(Product product, int count) {
        return new PurchasedProductResponse(
                product.getName(),
                count,
                product.getPrice() * count);
    }
}