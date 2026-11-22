package gdg.hongik.mission.controller;

import gdg.hongik.mission.ProductStore;
import gdg.hongik.mission.dto.AddStockRequest;
import gdg.hongik.mission.dto.AddStockResponse;
import gdg.hongik.mission.dto.CreateProductRequest;
import gdg.hongik.mission.dto.CreateProductResponse;
import gdg.hongik.mission.dto.DeleteProductRequest;
import gdg.hongik.mission.dto.DeleteProductResponse;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "*") // 모든 출처(프론트엔드)에서의 API 요청을 허용합니다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products")
@Tag(name = "관리자 상품 관리", description = "상품 등록, 재고 추가 및 상품 삭제 API")
public class ProductAdminController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "상품 등록",
            description = "여러 상품을 객체 배열로 등록합니다. 재고가 0 이하이거나 ID 또는 이름이 중복되면 "
                    + "전체 요청이 취소됩니다. 등록된 상품들과 총 가격을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "항목 등록 성공"),
            @ApiResponse(responseCode = "200", description = "실패하면 빈 배열"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<List<CreateProductResponse>> createProduct(@RequestBody List<CreateProductRequest> requests) {
        List<CreateProductResponse> responses = productService.createProduct(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PatchMapping("/stock")
    @Operation(summary = "상품 재고 추가",
            description = "상품명과 추가 수량을 객체 배열로 전달합니다. 수량은 1 이상이며, "
                    + "실패하면 전체가 취소됩니다. 재고 추가에 성공한 상품 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재고 추가 결과 (실패하면 빈 배열)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<List<AddStockResponse>> addStock(@RequestBody List<AddStockRequest> requests) {

        List<AddStockResponse> responses = productService.addStock(requests);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping
    @Operation(summary = "상품 삭제",
            description = "요청 본문의 상품명으로 여러 상품을 삭제합니다. "
                    + "실패하면 전체 롤백합니다. 삭제에 성공한 상품 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 결과 (실패하면 빈 배열)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<List<DeleteProductResponse>> deleteProduct(
            @RequestBody List<DeleteProductRequest> requests
    ) {
        List<DeleteProductResponse> responses = productService.deleteProduct(requests);

        return ResponseEntity.ok(responses);
    }
}
