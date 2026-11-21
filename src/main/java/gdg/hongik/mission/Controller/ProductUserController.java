package gdg.hongik.mission.Controller;

import gdg.hongik.mission.DTO.Response.ProductPruchaseResponse;
import gdg.hongik.mission.DTO.Request.ProductPurchase;
import gdg.hongik.mission.Entity.Product;
import gdg.hongik.mission.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin(origins = "*")
@RestController
@Tag(
        name = "사용자 API",
        description = "상품 조회 및 상품 구매 기능을 제공합니다."
)
public class ProductUserController {

    private final ProductService productService;

    public ProductUserController(ProductService productService) {
        this.productService = productService;
    }




    @Operation(
            summary = "상품명 단건 조회",
            description = "상품명을 이용하여 상품을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 조회 성공"
    )
    @GetMapping("/products")
    public List<Product> getProduct(@RequestParam(required = false) String itemName){
            if(itemName == null ){
                return productService.getAll();
            }
            return productService.getProduct(itemName);
    }




    @Operation(
            summary = "상품 구매",
            description = "구매할 상품 ID와 수량 목록을 입력받아 재고를 차감하고 총 구매 금액을 반환합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 구매 성공"
    )
    @PostMapping("/products/purchase")
    public ResponseEntity<ProductPruchaseResponse> purchase (@RequestBody List<ProductPurchase> purchaseList) {

        ProductPruchaseResponse response = productService.purchase(purchaseList);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}

