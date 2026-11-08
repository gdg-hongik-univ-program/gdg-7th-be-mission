package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.ProductStore;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/admin/products")
@Tag(name = "관리자 상품 관리", description = "상품 등록, 재고 추가 및 상품 삭제 API")
public class ProductAdminController {

    private final ProductStore productStore;
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "여러 상품 등록",
            description = "여러 상품을 객체 배열로 등록합니다. 재고가 0 이하이거나 ID 또는 이름이 중복되면 "
                    + "해당 상품은 건너뛰고, 등록에 성공한 상품 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "한 항목 이상 등록 성공 (부분 성공 포함)"),
            @ApiResponse(responseCode = "200", description = "모두 실패하면 빈 배열"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<Product> createProduct(@RequestBody Product.CreateProductRequest request) {
        Product product = productService.createProduct(request);

        return ResponseEntity.created(URI.create("/api/products/" + product.getId())).body(product);
    }

    @PatchMapping("/stock")
    @Operation(summary = "여러 상품 재고 추가",
            description = "상품 ID와 추가 수량을 객체 배열로 전달합니다. 수량은 1 이상이며, "
                    + "실패한 상품은 건너뛰고 재고 추가에 성공한 상품 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재고 추가 결과 (모두 실패하면 빈 배열)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
<<<<<<< HEAD
    public ResponseEntity<Product> addStock(@Parameter(hidden = true) @PathVariable Long productId,
                                            @RequestBody QuantityRequest request) {
        validateQuantity(request.quantity());
=======
    public ResponseEntity<Product> addStock(@PathVariable Long productId,
                                            @RequestBody Product.QuantityRequest request) {
>>>>>>> 09860731a42d36bf51fc05bc372199f0f9f16ccd

        Product.ProductResponse response = productService.addStock(productId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "여러 상품 삭제",
            description = "요청 본문의 productIds 배열로 여러 상품을 삭제합니다. "
                    + "없는 상품은 건너뛰고 삭제에 성공한 상품 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 결과 (모두 실패하면 빈 배열)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<Product> deleteProduct(@Parameter(hidden = true) @PathVariable Long productId) {
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
