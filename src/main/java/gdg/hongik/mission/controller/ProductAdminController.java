package gdg.hongik.mission.controller;

import gdg.hongik.mission.dto.AddStockRequest;
import gdg.hongik.mission.dto.DeleteProductsRequest;
import gdg.hongik.mission.dto.ProductRequest;
import gdg.hongik.mission.dto.ProductResponse;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin/products")
@Tag(name = "관리자 상품 API", description = "상품 등록, 재고 추가, 삭제")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @Operation(summary = "상품 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @Operation(summary = "상품명으로 재고 추가")
    @PatchMapping("/stock")
    public ProductResponse addStock(@RequestBody AddStockRequest request) {
        return productService.addStock(request.itemName(), request.quantity());
    }

    @Operation(summary = "상품 ID 목록으로 삭제하고 남은 상품 조회")
    @DeleteMapping
    public List<ProductResponse> deleteProducts(@RequestParam("ids") List<Long> ids) {
        return productService.deleteProducts(ids);
    }
}