package gdg.hongik.mission.controller;

import gdg.hongik.mission.dto.request.PurchaseItemRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.dto.response.PurchaseResponse;
import gdg.hongik.mission.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductUserController {

    private final ProductService productService;

    // 1.1 전체 상품 목록 조회: GET /products
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    // 1.2 상품명 단건 조회: GET /products?itemName=원피스
    @GetMapping(params = "itemName")
    public ProductResponse getProduct(@RequestParam String itemName) {
        return productService.getProduct(itemName);
    }

    // 1.3 장바구니 일괄 구매: POST /products/purchase
    @PostMapping("/purchase")
    public PurchaseResponse purchase(@RequestBody List<PurchaseItemRequest> requests) {
        return productService.purchase(requests);
    }
}
