package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    // 상품 등록
    @Operation(
            summary = "상품 등록",
            description = "상품명(name), 개당 가격(price), "
                    + "초기 재고(stockQuantity)를 입력하여 상품을 등록합니다. "
                    + "동일한 상품명이 존재하면 예외가 발생합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 등록 성공. 응답 본문은 없습니다."
    )
    @PostMapping
    public ResponseEntity<Void> createProduct(
            @RequestBody Product request
    ) {
        productService.createProduct(request);

        return ResponseEntity.status(201).build();
    }

    // 재고 추가
    @Operation(
            summary = "재고 추가",
            description = "경로의 productId에 해당하는 상품에 "
                    + "요청 본문의 quantityToAdd만큼 재고를 추가합니다. "
                    + "상품명과 추가 후 최종 재고 수량을 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "재고 추가 성공"
    )
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<Map<String, Object>> addStock(
            @PathVariable("productId") Long productId,
            @RequestBody Map<String, Integer> request
    ) {
        Map<String, Object> response = productService.addStock(
                productId,
                request.get("quantityToAdd")
        );

        return ResponseEntity.ok(response);
    }

    // 상품 삭제
    @Operation(
            summary = "상품 삭제",
            description = "요청 본문의 ids 배열에 담긴 상품들을 삭제합니다. "
                    + "삭제 후 남아 있는 상품들의 이름과 재고 수량을 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 삭제 성공"
    )
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteProducts(
            @RequestBody Map<String, List<Long>> request
    ) {
        Map<String, Object> response =
                productService.deleteProducts(request.get("ids"));

        return ResponseEntity.ok(response);
    }
}