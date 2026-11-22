package gdg.hongik.mission.controller;

import gdg.hongik.mission.dto.request.ProductCreateRequest;
import gdg.hongik.mission.dto.request.ProductDeleteRequest;
import gdg.hongik.mission.dto.request.StockUpdateRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    // 2.1 상품 등록: POST /admin/products
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    // 2.2 재고 추가: PATCH /admin/products/stock
    @PatchMapping("/stock")
    public ProductResponse addStock(@RequestBody StockUpdateRequest request) {
        return productService.addStock(request);
    }

    // 2.3 상품 삭제: DELETE /admin/products
    @DeleteMapping
    public List<ProductResponse> deleteProduct(@RequestBody ProductDeleteRequest request) {
        return productService.deleteProduct(request);
    }
}