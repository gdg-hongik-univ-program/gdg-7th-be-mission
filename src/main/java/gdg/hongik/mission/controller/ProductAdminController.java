package gdg.hongik.mission.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gdg.hongik.mission.dto.request.ProductCreateRequest;
import gdg.hongik.mission.dto.request.ProductDeleteRequest;
import gdg.hongik.mission.dto.request.StockUpdateRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.service.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductAdminController { // 관리자용 API Controller[cite: 2]

    private final ProductService productService; // 서비스 계층 주입[cite: 2]

    // 2-1. 상품 등록
    @PostMapping
    public ProductResponse registerProduct(@RequestBody ProductCreateRequest request) {
        return productService.registerProduct(request);
    }

    // 2-2. 재고 추가 (Request Body 이용)
    @PatchMapping("/stock")
    public ProductResponse addStock(@RequestBody StockUpdateRequest request) {
        return productService.addStock(request);
    }

    // 2-3. 상품 삭제
    @DeleteMapping
    public List<ProductResponse> deleteProducts(@RequestBody ProductDeleteRequest request) {
        return productService.deleteProducts(request);
    }
}