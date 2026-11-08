package gdg.hongik.mission.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gdg.hongik.mission.service.ProductService;
/*import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;*/
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductAdminController { // 관리자용 API Controller

    private final ProductService productService; // 서비스 계층 주입

    // 2-1. 상품 등록
    @PostMapping
    public Map<String, Object> registerProduct(@RequestBody ProductCreateRequest request) {
        return productService.registerProduct(request.getName(), request.getPrice(), request.getStockQuantity());
    }

    // 2-2. 재고 추가 (Request 1: Body 이용)
    @PatchMapping("/stock")
    public Map<String, Object> addStock(@RequestBody StockAddRequest request) {
        return productService.addStock(request.getProductId(), request.getQuantityToAdd());
    }

    // 2-3. 상품 삭제
    @DeleteMapping
    public Map<String, Object> deleteProducts(@RequestBody DeleteRequest request) {
        return productService.deleteProducts(request.getProductIds());
    }

    // 요청 데이터를 받기 위한 DTO 클래스들
    @Getter
    @Setter
    public static class ProductCreateRequest {
        private String name;
        private int price;
        private int stockQuantity;
    }

    @Getter
    @Setter
    public static class StockAddRequest {
        private Long productId;
        private int quantityToAdd;
    }

    @Getter
    @Setter
    public static class DeleteRequest {
        private List<Long> productIds;
    }
}