package gdg.hongik.mission.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gdg.hongik.mission.Product;
import gdg.hongik.mission.ProductStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;

@Tag(name = "2. 관리자 (Admin) API", description = "관리자용 상품 등록, 재고 관리, 상품 삭제 API")
@RestController
@RequestMapping("/api/v1/admin/products")
public class ProductAdminController { // 관리자용 API Controller

    // 2-1. 상품 등록
    @Operation(summary = "2-1. 상품 등록", description = "새로운 상품을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값 (가격/재고 음수)"),
            @ApiResponse(responseCode = "409", description = "동일 상품명 존재")
    })
    @PostMapping
    public Product registerProduct(@RequestBody ProductCreateRequest request) {
        if (request.getPrice() < 0 || request.getStockQuantity() < 0) {
            throw new RuntimeException("가격과 재고는 0 이상이어야 합니다.");
        }

        boolean isDuplicate = ProductStore.products.stream()
                .anyMatch(p -> p.getName().equals(request.getName()));
        if (isDuplicate) {
            throw new RuntimeException("이미 존재하는 상품명입니다: " + request.getName());
        }

        Product newProduct = new Product(ProductStore.nextId(), request.getName(), request.getPrice(),
                request.getStockQuantity());
        ProductStore.products.add(newProduct);

        return newProduct;
    }

    // 2-2. 재고 추가 (Request 1: Body 이용)
    @Operation(summary = "2-2. 재고 추가", description = "기존 상품의 재고를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재고 추가 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "productName": "무선 마우스",
                      "finalStockQuantity": 80
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "수량 오류 (1개 미만)"),
            @ApiResponse(responseCode = "404", description = "등록되지 않은 상품")
    })
    @PatchMapping("/stock")
    public Map<String, Object> addStock(@RequestBody StockAddRequest request) {
        if (request.getQuantityToAdd() <= 0) {
            throw new RuntimeException("추가할 재고 수량은 1개 이상이어야 합니다.");
        }

        Product product = ProductStore.products.stream()
                .filter(p -> p.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("재고를 추가할 상품(ID: " + request.getProductId() + ")이 존재하지 않습니다."));

        product.setStockQuantity(product.getStockQuantity() + request.getQuantityToAdd());

        return Map.of(
                "productName", product.getName(),
                "finalStockQuantity", product.getStockQuantity());
    }

    // 2-3. 상품 삭제
    @Operation(summary = "2-3. 상품 삭제", description = "하나 이상의 상품을 한 번에 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "remainingProducts": [
                        {
                          "productName": "게이밍 헤드셋",
                          "stockQuantity": 15
                        },
                        {
                          "productName": "USB Hub",
                          "stockQuantity": 100
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 삭제 시도")
    })
    @DeleteMapping
    public Map<String, Object> deleteProducts(@RequestBody DeleteRequest request) {
        List<Long> idsToDelete = request.getProductIds();

        if (idsToDelete == null || idsToDelete.isEmpty()) {
            throw new RuntimeException("삭제할 상품 ID 목록이 비어있습니다.");
        }

        // 상품 존재 여부 검증
        for (Long id : idsToDelete) {
            boolean exists = ProductStore.products.stream().anyMatch(p -> p.getProductId().equals(id));
            if (!exists) {
                throw new RuntimeException("삭제하려는 상품 중 일부 또는 전체가 존재하지 않습니다. (ID: " + id + ")");
            }
        }

        // 삭제 처리
        ProductStore.products.removeIf(p -> idsToDelete.contains(p.getProductId()));

        // 남은 상품 응답
        List<Map<String, Object>> remainingProducts = ProductStore.products.stream()
                .map(p -> Map.<String, Object>of(
                        "productName", p.getName(),
                        "stockQuantity", p.getStockQuantity()))
                .collect(Collectors.toList());

        return Map.of("remainingProducts", remainingProducts);
    }

    // 요청 데이터를 받기 위한 DTO 클래스들
    @Getter
    @Setter
    @Schema(description = "상품 등록 요청 정보")
    public static class ProductCreateRequest {
        @Schema(description = "상품명", example = "무선 마우스")
        private String name;

        @Schema(description = "가격", example = "25000")
        private int price;

        @Schema(description = "초기 재고 수량", example = "50")
        private int stockQuantity;
    }

    @Getter
    @Setter
    @Schema(description = "재고 추가 요청 정보")
    public static class StockAddRequest {
        @Schema(description = "상품 ID", example = "101")
        private Long productId;

        @Schema(description = "추가할 재고 수량", example = "30")
        private int quantityToAdd;
    }

    @Getter
    @Setter
    @Schema(description = "상품 삭제 요청 정보")
    public static class DeleteRequest {
        @Schema(description = "삭제할 상품 ID 목록", example = "[101, 102]")
        private List<Long> productIds;
    }
}