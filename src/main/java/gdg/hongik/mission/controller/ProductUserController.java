package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.ProductStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "소비자 상품", description = "소비자용 상품 조회 및 구매 API")
public class ProductUserController {

    private final ProductStore productStore;

    @GetMapping
    @Operation(summary = "특정 상품 조회", description = "상품의 ID, 상품명, 가격 및 재고를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<Product> getProduct(String name) {
        Product result =  productStore.getProducts().stream()
                .filter(product -> product.getName().equals(name))
                .findFirst().orElseThrow(()->new RuntimeException("상품을 찾을 수 없습니다."));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{productId}/purchases")
    @Operation(summary = "상품 구매", description = "상품 ID와 구매 수량을 이용하여 상품을 구매하고 재고를 차감합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 구매 성공"),
            @ApiResponse(responseCode = "400", description = "구매 수량이 올바르지 않거나 재고가 부족함"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<PurchaseResponse> purchaseProduct(@PathVariable Long productId,
                                                            @RequestBody PurchaseRequest request) {
        if (request.quantity() <= 0) {
            throw new RuntimeException("구매 수량은 1 이상이어야 합니다.");
        }

        Product product = productStore.getProducts().stream()
                .filter(savedProduct -> savedProduct.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        if (product.getStock() < request.quantity()) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        product.removeStock(request.quantity());

        PurchaseResponse response = new PurchaseResponse(
                product.getId(),
                product.getName(),
                request.quantity(),
                product.getPrice() * request.quantity(),
                product.getStock()
        );
        return ResponseEntity.ok(response);
    }

    public record PurchaseRequest(int quantity) {
    }

    public record PurchaseResponse(Long productId,
                                   String productName,
                                   int quantity,
                                   int totalPrice,
                                   int remainingStock) {
    }
}
