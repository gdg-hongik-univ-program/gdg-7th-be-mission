package gdg.hongik.mission.controller;

import gdg.hongik.mission.dto.AddStockRequest;
import gdg.hongik.mission.dto.AddStockResponse;
import gdg.hongik.mission.dto.CreateProductRequest;
import gdg.hongik.mission.dto.CreateProductResponse;
import gdg.hongik.mission.dto.DeleteProductRequest;
import gdg.hongik.mission.dto.DeleteProductResponse;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            description = "여러 상품을 객체 배열로 등록합니다. 상품명과 카테고리는 필수이며 가격과 초기 재고는 0 이상입니다. 카테고리는 의류, 전자기기, 화장품, 식품 중 하나입니다. "
                    + "하나라도 실패하면 전체 롤백합니다. 등록된 상품 목록을 카테고리와 함께 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "항목 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이미 등록된 상품명 (전체 취소)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<List<CreateProductResponse>> createProduct(
            @RequestBody
            @NotEmpty(message = "요청 목록은 비어 있을 수 없습니다.")
            @Valid List<@NotNull(message = "요청 항목은 null일 수 없습니다.") @Valid CreateProductRequest> requests
    ) {
        List<CreateProductResponse> responses = productService.createProduct(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PatchMapping("/stock")
    @Operation(summary = "상품 재고 추가",
            description = "상품명과 추가 수량을 객체 배열로 전달합니다. 수량은 1 이상이며, "
                    + "실패하면 전체가 취소됩니다. 재고 추가에 성공한 상품 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재고 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (전체 취소)"),
            @ApiResponse(responseCode = "404", description = "상품 없음 (전체 취소)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<List<AddStockResponse>> addStock(
            @RequestBody
            @NotEmpty(message = "요청 목록은 비어 있을 수 없습니다.")
            @Valid List<@NotNull(message = "요청 항목은 null일 수 없습니다.") @Valid AddStockRequest> requests
    ) {

        List<AddStockResponse> responses = productService.addStock(requests);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping
    @Operation(summary = "상품 삭제",
            description = "요청 본문의 상품명으로 여러 상품을 삭제합니다. "
                    + "실패하면 전체 롤백합니다. 삭제한 상품 목록을 카테고리와 함께 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (전체 취소)"),
            @ApiResponse(responseCode = "404", description = "상품 없음 (전체 취소)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    public ResponseEntity<List<DeleteProductResponse>> deleteProduct(
            @RequestBody
            @NotEmpty(message = "요청 목록은 비어 있을 수 없습니다.")
            @Valid List<@NotNull(message = "요청 항목은 null일 수 없습니다.") @Valid DeleteProductRequest> requests
    ) {
        List<DeleteProductResponse> responses = productService.deleteProduct(requests);

        return ResponseEntity.ok(responses);
    }
}
