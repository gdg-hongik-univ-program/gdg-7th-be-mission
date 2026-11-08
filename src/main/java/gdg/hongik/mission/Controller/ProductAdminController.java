package gdg.hongik.mission.Controller;

import gdg.hongik.mission.Entity.Product;
import gdg.hongik.mission.Repository.ProductRepository;
import gdg.hongik.mission.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Tag(
        name = "관리자 API",
        description = "상품 등록, 재고 추가, 상품 삭제 기능을 제공합니다."
)
public class ProductAdminController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductAdminController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
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
    @PostMapping("/api/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {

        Product newProduct = productService.createProduct(product);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newProduct);

    }

    @Operation(
            summary = "상품 재고 추가",
            description = "상품 ID와 추가 수량을 입력받아 기존 상품의 재고를 증가시킵니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "재고 추가 성공"
    )
    @PostMapping("/api/stock")
    public Map<String,Object> addStock(@RequestBody Map<String, Object> request){

        Long productId = ((Number) request.get("productId")).longValue();
        int quantity = ((Number) request.get("quantity")).intValue();

        Map<String, Object> response = new HashMap<>();

        Product product = productService.addStock(productId,quantity);

        response.put("name",product.getName());
        response.put("stock",product.getStock() );

        return response;
    }


    @Operation(
            summary = "상품 삭제",
            description = "상품 ID 목록을 입력받아 해당 상품들을 삭제하고 남은 상품 목록을 반환합니다."
    )

    @ApiResponse(
            responseCode = "200",
            description = "상품 삭제 성공"
    )
    @DeleteMapping("/api/products")
    public Map<String, Object> deleteProducts(@RequestBody Map<String, Object> request) {

        List<Map<String,Object>> items = (List<Map<String, Object>>) request.get("products");
        List<Product> products = productService.deleteProducts(items);
        List<Map<String, Object>> remainingProducts = new ArrayList<>();

        for(Product product : products){
            Map<String, Object> remainingProduct = new HashMap<>();
            remainingProduct.put("name", product.getName());
            remainingProduct.put("stock", product.getStock());
            remainingProducts.add(remainingProduct);
        }


        Map<String, Object> response = new HashMap<>();
        response.put("products", remainingProducts);

        return response;
    }
}
