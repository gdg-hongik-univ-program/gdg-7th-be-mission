package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.ProductStore;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@Tag(name = "관리자 상품 관리", description = "상품 등록, 재고 추가 및 상품 삭제 API")
public class ProductAdminController {

    private final ProductStore productStore;
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "상품 등록", description = "상품 ID, 상품명, 가격, 초기 재고를 입력하여 상품을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "상품 입력값이 올바르지 않음"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 상품 ID")
    })
    public ResponseEntity<Product> createProduct(@RequestBody Product.CreateProductRequest request) {
        Product product = productService.createProduct(request);

        return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(product);
    }

    @PatchMapping("/{productId}/stock")
    @Operation(summary = "상품 재고 추가", description = "상품 ID에 해당하는 상품의 재고를 입력한 수량만큼 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재고 추가 성공"),
            @ApiResponse(responseCode = "400", description = "수량이 올바르지 않음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<Product> addStock(@PathVariable Long productId,
                                            @RequestBody Product.QuantityRequest request) {

        Product.ProductResponse response = productService.addStock(productId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품 ID에 해당하는 상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "삭제 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<Product> deleteProduct(@PathVariable Long productId) {
        Product deletedProduct = findProduct(productId);
        productStore.getProducts().remove(deletedProduct);

        return ResponseEntity.ok(deletedProduct);
    }

    private Product findProduct(Long productId) {
        return productStore.getProducts().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("수량은 1 이상이어야 합니다.");
        }
    }

}
