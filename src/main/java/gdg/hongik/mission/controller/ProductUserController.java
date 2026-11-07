package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 소비자(User)용 상품 조회 및 구매 API 요청을 처리하는 컨트롤러입니다.
 * 요청과 응답 처리에만 집중하며, 실제 비즈니스 로직은 ProductService에게 맡깁니다.
 */
@RestController
@RequestMapping("/products")
public class ProductUserController {

    private final ProductService productService;

    /**
     * 생성자를 통해 스프링이 관리하는 ProductService 빈(Bean)을 주입받습니다.
     */
    public ProductUserController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 1. 상품 조회 API (GET /products?name=상품명)
     */
    @GetMapping
    public Product getProductByName(@RequestParam String name) {
        return productService.getProductByName(name);
    }

    /**
     * 2. 상품 구매 API (POST /products/purchase)
     */
    @PostMapping("/purchase")
    public Map<String, Object> purchaseProducts(@RequestBody List<Product> requests) {
        return productService.purchaseProducts(requests);
    }
}
