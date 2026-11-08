package gdg.hongik.mission.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gdg.hongik.mission.service.ProductService;
/*import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductUserController { // 소비자용 API Controller

    private final ProductService productService; // 서비스 계층 주입

    // 1-1. 상품 조회
    @GetMapping("/products/search")
    public Map<String, Object> searchProduct(@RequestParam String name) {
        return productService.searchProductByName(name);
    }

    // 1-2. 상품 구매
    @PostMapping("/orders")
    public Map<String, Object> orderProducts(@RequestBody OrderRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("구매할 상품 목록이 비어있습니다.");
        }

        return productService.orderProducts(request.getItems()); // 상품 구매 로직을 서비스 계층으로 위임
    }

    @Getter
    @Setter
    public static class OrderRequest {
        private List<OrderItem> items;
    }

    @Getter
    @Setter
    public static class OrderItem {
        private Long productId;
        private int quantity;
    }
}