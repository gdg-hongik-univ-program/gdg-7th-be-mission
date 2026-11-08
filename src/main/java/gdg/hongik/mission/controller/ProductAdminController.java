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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products")
@Tag(name = "관리자 상품 관리", description = "상품 등록, 재고 추가 및 상품 삭제 API")
public class ProductAdminController {

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
    public ResponseEntity<List<Product.CreateProductResponse>> createProduct(@RequestBody List<Product.CreateProductRequest> requests) {
        List<Product.CreateProductResponse> responses = productService.createProduct(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
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
    public ResponseEntity<List<Product.AddStockResponse>> addStock(@RequestBody List<Product.AddStockRequest> requests) {

        List<Product.AddStockResponse> responses = productService.addStock(requests);

        return ResponseEntity.ok(responses);
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
    public ResponseEntity<List<Product.DeleteProductResponse>> deleteProduct(
            @RequestBody List<Product.DeleteProductRequest> requests
    ) {
        List<Product.DeleteProductResponse> responses = productService.deleteProduct(requests);

        return ResponseEntity.ok(responses);
    }
}
