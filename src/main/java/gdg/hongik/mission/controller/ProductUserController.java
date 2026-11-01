package gdg.hongik.mission.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gdg.hongik.mission.Product;
import gdg.hongik.mission.ProductStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;

@Tag(name = "1. 소비자 (User) API", description = "소비자용 상품 조회 및 주문 API")
@RestController
@RequestMapping("/api/v1")
public class ProductUserController { // 소비자용 API Controller

    // 1-1. 상품 조회
    @Operation(summary = "1-1. 상품 조회", description = "특정 상품명으로 상품 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "등록되지 않은 상품명")
    })
    @GetMapping("/products/search")
    public Product searchProduct(@RequestParam String name) {
        return ProductStore.products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 이름의 상품을 찾을 수 없습니다."));
    }

    // 1-2. 상품 구매
    @Operation(summary = "1-2. 상품 구매", description = "하나 이상의 상품을 한 번에 구매합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "구매 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "totalAmount": 85000,
                      "purchasedItems": [
                        {
                          "productName": "무선 마우스",
                          "quantity": 2,
                          "subtotalAmount": 50000
                        },
                        {
                          "productName": "기계식 키보드",
                          "quantity": 1,
                          "subtotalAmount": 35000
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "수량 오류 (0 이하) 또는 재고 부족"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 ID")
    })
    @PostMapping("/orders")
    public Map<String, Object> orderProducts(
            @Parameter(description = "조회할 상품명", example = "무선 마우스", required = true) @RequestBody OrderRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("구매할 상품 목록이 비어있습니다.");
        }

        // STEP 1: 사전 검증
        for (OrderItem item : request.getItems()) {
            if (item.getQuantity() <= 0) {
                throw new RuntimeException("구매 수량은 1개 이상이어야 합니다.");
            }

            Product product = ProductStore.products.stream()
                    .filter(p -> p.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("요청한 상품(ID: " + item.getProductId() + ")이 존재하지 않습니다."));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException(product.getName() + " 상품의 재고가 부족합니다.");
            }
        }

        // STEP 2: 재고 차감 및 응답 생성
        int totalAmount = 0;
        List<Map<String, Object>> purchasedItems = new ArrayList<>();

        for (OrderItem item : request.getItems()) {
            Product product = ProductStore.products.stream()
                    .filter(p -> p.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .get();

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

            int subtotal = product.getPrice() * item.getQuantity();
            totalAmount += subtotal;

            purchasedItems.add(Map.of(
                    "productName", product.getName(),
                    "quantity", item.getQuantity(),
                    "subtotalAmount", subtotal));
        }

        return Map.of(
                "totalAmount", totalAmount,
                "purchasedItems", purchasedItems);
    }

    @Getter
    @Setter
    @Schema(description = "상품 주문 요청 정보")
    public static class OrderRequest {
        @Schema(description = "주문 상품 목록")
        private List<OrderItem> items;
    }

    @Getter
    @Setter
    @Schema(description = "주문 항목 정보")
    public static class OrderItem {
        @Schema(description = "상품 ID", example = "101")
        private Long productId;

        @Schema(description = "구매 수량", example = "2")
        private int quantity;
    }
}