package gdg.hongik.mission.Controller;

import gdg.hongik.mission.Product;
import gdg.hongik.mission.ProductStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Tag(
        name = "사용자 API",
        description = "상품 조회 및 상품 구매 기능을 제공합니다."
)
public class ProductUserController {

    @Operation(
            summary = "상품 조회",
            description = "상품명을 이용하여 상품의 ID, 이름, 가격, 재고를 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 조회 성공"
    )
    @GetMapping("/api/products")
    public Product getProduct(@RequestParam("name") String name){

        for(Product product : ProductStore.products){
            if(product.getName().equals(name)){
                return product;
            }
        }

        throw new RuntimeException("해당 이름의 상품이 없습니다.");

    }


    @Operation(
            summary = "상품 구매",
            description = "구매할 상품 ID와 수량 목록을 입력받아 재고를 차감하고 총 구매 금액을 반환합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 구매 성공"
    )
    @PostMapping("/api/orders")

    public ResponseEntity<Map<String, Object>> purchase(
            @RequestBody Map<String, Object> request) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("products");

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("구매할 상품이 없습니다.");
        }

        long totalPrice = 0;

        List<Map<String, Object>> purchasedProducts = new ArrayList<>();

        // 재고 변경 전에 구매 요청 전체를 검사

        Map<Long, Long> requestedQuantities = new HashMap<>();

        for (Map<String, Object> item : items) {

            Long productId =
                    ((Number) item.get("productId")).longValue();

            int quantity =
                    ((Number) item.get("quantity")).intValue();

            if (quantity <= 0) {
                throw new IllegalArgumentException("구매 수량은 1 이상이어야 합니다.");
            }

            Product foundProduct = null;

            for (Product product : ProductStore.products) {
                if (product.getId().equals(productId)) {
                    foundProduct = product;
                    break;
                }
            }

            if (foundProduct == null) {
                throw new RuntimeException("상품을 찾을 수 없습니다.");
            }

            long accumulatedQuantity =
                    requestedQuantities.getOrDefault(productId, 0L) + quantity;

            if (foundProduct.getStock() < accumulatedQuantity) {
                throw new RuntimeException("재고가 부족합니다.");
            }

            requestedQuantities.put(productId, accumulatedQuantity);

            long amount = (long) foundProduct.getPrice() * quantity;

            totalPrice += amount;

            Map<String, Object> purchasedItem = new HashMap<>();

            purchasedItem.put("name", foundProduct.getName());
            purchasedItem.put("quantity", quantity);
            purchasedItem.put("amount", amount);

            purchasedProducts.add(purchasedItem);

        }

        // 모든 상품 검사가 끝난 후 재고 감소

        for (Product product : ProductStore.products) {

            long quantity =
                    requestedQuantities.getOrDefault(product.getId(), 0L);

            if (quantity > 0) {
                product.setStock(
                        (int) (product.getStock() - quantity)
                );
            }
        }

        Map<String, Object> response = new HashMap<>();

        response.put("totalPrice", totalPrice);
        response.put("products", purchasedProducts);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

}

