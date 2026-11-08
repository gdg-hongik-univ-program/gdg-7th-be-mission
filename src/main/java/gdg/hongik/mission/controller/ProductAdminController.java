package gdg.hongik.mission.controller;

import gdg.hongik.mission.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    // 1. 상품 등록: POST /api/admin/products
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createProduct(@RequestBody Map<String, Object> body) {
        Object nameValue = body.get("name");
        if (!(nameValue instanceof String) || ((String) nameValue).isBlank()) {
            throw new RuntimeException("상품명은 비어 있을 수 없습니다.");
        }
        String name = (String) nameValue;
        long price = toLong(body.get("price"));
        long stock = toLong(body.get("stock"));

        return productService.createProduct(name, price, stock);
    }

    // 2. 재고 추가: PATCH /api/admin/products/{productId}/stock
    @PatchMapping("/products/{productId}/stock")
    public Map<String, Object> addStock(@PathVariable Long productId,
                                        @RequestBody Map<String, Object> body) {
        long quantity = toLong(body.get("quantity"));

        return productService.addStock(productId, quantity);
    }

    // 3. 상품 삭제: DELETE /api/admin/products
    @SuppressWarnings("unchecked")
    @DeleteMapping("/products")
    public Map<String, Object> deleteProducts(@RequestBody Map<String, Object> body) {
        List<Object> productIds = (List<Object>) body.get("productIds");

        List<Long> ids = new ArrayList<>();
        if (productIds != null) {
            for (Object value : productIds) {
                ids.add(toLong(value));
            }
        }

        return productService.deleteProducts(ids);
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new RuntimeException("숫자 값이 필요합니다.");
    }
}
