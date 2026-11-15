package gdg.hongik.mission.controller;

import gdg.hongik.mission.dto.ProductResponse;
import gdg.hongik.mission.dto.PurchaseRequest;
import gdg.hongik.mission.dto.PurchaseResponse;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
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

    @Operation(
            summary = "상품 조회",
            description = "이름으로 상품 하나를 조회하고 상품 ID, 이름, 개당 가격, 재고 수량을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    @GetMapping("/products")
    public ResponseEntity<ProductResponse> getProduct(@RequestParam("name") String name) {
        Product product = productService.findOneByName(name);
        ProductResponse response = new ProductResponse(
                product.getId(),
                product.getItemName(),
                product.getPrice(),
                product.getQuantity()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "상품 구매",
            description = "items 배열에 productId와 quantity를 담아 하나 이상의 상품을 구매합니다. "
                    + "총 구매 금액과 상품별 이름, 구매 수량, 구매 금액을 반환합니다. "
                    + "상품이 없거나 재고가 부족하면 예외가 발생합니다."
    )
    @ApiResponse(responseCode = "201", description = "상품 구매 성공")
    @PostMapping("/purchases")
    public ResponseEntity<PurchaseResponse> purchase(@RequestBody PurchaseRequest request) {
        PurchaseResponse response = productService.purchaseProducts(request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}