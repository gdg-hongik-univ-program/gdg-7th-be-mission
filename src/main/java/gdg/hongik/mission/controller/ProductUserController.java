package gdg.hongik.mission.controller;

import gdg.hongik.mission.Service.ProductService;
import gdg.hongik.mission.Service.ProductService.PurchaseItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductUserController {

    private final ProductService productService;

    // 1. 상품 조회: GET /api/products?name=키보드
    @GetMapping("/products")
    public Map<String, Object> getProduct(@RequestParam String name) {
        return productService.getProduct(name);
    }

    // 2. 상품 구매: POST /api/orders
    @SuppressWarnings("unchecked")
    @PostMapping("/orders")
    public Map<String, Object> purchase(@RequestBody Map<String, Object> body) {
        List<Map<String, Object>> requestItems = (List<Map<String, Object>>) body.get("items");

        List<PurchaseItem> items = new ArrayList<>();
        if (requestItems != null) {
            for (Map<String, Object> item : requestItems) {
                items.add(new PurchaseItem(toLong(item.get("productId")), toLong(item.get("quantity"))));
            }
        }

        return productService.purchase(items);
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new RuntimeException("숫자 값이 필요합니다.");
    }
}
