package gdg.hongik.mission.Controller;

import gdg.hongik.mission.DTO.Request.ProductAddStock;
import gdg.hongik.mission.DTO.Request.ProductDelete;
import gdg.hongik.mission.DTO.Request.ProductRequest;
import gdg.hongik.mission.Entity.Product;
import gdg.hongik.mission.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Tag(
        name = "관리자 API",
        description = "상품 등록, 재고 추가, 상품 삭제 기능을 제공합니다."
)
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController( ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "상품 등록",
            description = "상품명, 가격, 재고를 입력받아 새로운 상품을 등록합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 등록 성공"
    )
    @PostMapping("/admin/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {

        Product product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);

    }

    @Operation(
            summary = "상품 재고 추가",
            description = "상품 ID와 추가 수량을 입력받아 기존 상품의 재고를 증가시킵니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "재고 추가 성공"
    )
    @PatchMapping("/admin/products/stock")
    public Product addStock(@RequestBody ProductAddStock item){

        String productName = item.itemName();
        int quantity = item.quantity();;

        Product product = productService.addStock(productName,quantity);

        return product;
    }


    @Operation(
            summary = "상품 삭제",
            description = "상품 ID 목록을 입력받아 해당 상품들을 삭제하고 남은 상품 목록을 반환합니다."
    )

    @ApiResponse(
            responseCode = "200",
            description = "상품 삭제 성공"
    )
    @DeleteMapping("/admin/products")
    public List<Product> deleteProducts(@RequestBody ProductDelete items) {

        List<Product> products = productService.deleteProducts(items);

        return products;
    }


}
