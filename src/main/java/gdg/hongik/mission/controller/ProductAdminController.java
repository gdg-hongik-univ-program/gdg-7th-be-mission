package gdg.hongik.mission.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
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
import gdg.hongik.mission.service.ProductAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    // 2-1. 상품 등록
    @PostMapping
    public ProductResponse registerProduct(@Valid @RequestBody ProductCreateRequest request) {
        return productAdminService.registerProduct(request);
    }

    // 2-2. 재고 추가
    @PatchMapping("/stock")
    public ProductResponse addStock(@Valid @RequestBody StockUpdateRequest request) {
        return productAdminService.addStock(request);
    }

    // 2-3. 상품 삭제
    @DeleteMapping
    public List<ProductResponse> deleteProduct(
            @Valid @RequestBody ProductDeleteRequest request) {
        return productAdminService.deleteProduct(request);
    }
}