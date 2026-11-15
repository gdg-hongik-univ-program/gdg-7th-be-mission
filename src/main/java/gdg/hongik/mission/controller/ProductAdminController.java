package gdg.hongik.mission.controller;

import gdg.hongik.mission.dto.AddStockRequest;
import gdg.hongik.mission.dto.DeleteProductsRequest;
import gdg.hongik.mission.dto.DeleteProductsResponse;
import gdg.hongik.mission.dto.ProductRequest;
import gdg.hongik.mission.dto.ProductStockResponse;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/products")
@Tag(
        name = "관리자 상품 API",
        description = "관리자의 상품 등록, 재고 추가, 삭제 기능"
)
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "상품 등록",
            description = "상품명(name), 개당 가격(price), 초기 재고(stockQuantity)로 상품을 등록합니다. "
                    + "동일한 상품명이 존재하면 예외가 발생합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 등록 성공. 응답 본문은 없습니다."
    )
    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "재고 추가",
            description = "경로의 productId에 해당하는 상품의 재고를 quantityToAdd만큼 추가하고 "
                    + "상품명과 최종 재고를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "재고 추가 성공")
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ProductStockResponse> addStock(
            @PathVariable("productId") Long productId,
            @RequestBody AddStockRequest request
    ) {
        ProductStockResponse response = productService.addStock(
                productId,
                request.quantityToAdd()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "상품 삭제",
            description = "요청 본문의 ids에 담긴 상품들을 삭제하고 "
                    + "남은 상품의 이름과 재고 수량을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "상품 삭제 성공")
    @DeleteMapping
    public ResponseEntity<DeleteProductsResponse> deleteProducts(
            @RequestBody DeleteProductsRequest request
    ) {
        DeleteProductsResponse response = productService.deleteProducts(request.ids());
        return ResponseEntity.ok(response);
    }
}