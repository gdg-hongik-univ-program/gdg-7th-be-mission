package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Tag(
        name = "소비자 상품 API",
        description = "소비자의 상품 조회와 구매 기능"
)
public class ProductUserController {

    private final ProductService productService;

    public ProductUserController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 조회
    @Operation(
            summary = "상품 조회",
            description = "상품명과 정확히 일치하는 상품 하나를 조회합니다. "
                    + "상품 ID, 상품명, 개당 가격, 현재 재고 수량을 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 조회 성공"
    )
    @GetMapping("/products")
    public ResponseEntity<Product> getProduct(
            @RequestParam("name") String name
    ) {
        Product product = productService.findOneByName(name);

        return ResponseEntity.ok(product);
    }

    // 상품 구매
    @Operation(
            summary = "상품 구매",
            description = "items 배열에 productId와 quantity를 담아 "
                    + "하나 이상의 상품을 구매합니다. "
                    + "총 구매 금액과 상품별 이름, 구매 수량, 소비 금액을 반환합니다. "
                    + "상품이 없거나 재고가 부족하면 예외가 발생합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 구매 성공"
    )
    @PostMapping("/purchases")
    public ResponseEntity<Map<String, Object>> purchase(
            @RequestBody Map<String, List<Map<String, Long>>> request
    ) {
        Map<String, Object> response =
                productService.purchaseProducts(request.get("items"));

        return ResponseEntity.status(201).body(response);
    }
}