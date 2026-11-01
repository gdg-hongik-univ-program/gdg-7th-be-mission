package gdg.hongik.mission.Controller;

import gdg.hongik.mission.Product;
import gdg.hongik.mission.ProductStore;
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

        if (product.getName() == null || product.getName().isBlank()) {
            throw new RuntimeException("상품명을 입력해주세요.");
        }

        if (product.getPrice() < 0 || product.getStock() < 0) {
            throw new RuntimeException("가격과 재고는 0 이상이어야 합니다.");
        }

        for (Product existingProduct : ProductStore.products) {
            if (existingProduct.getName().equals(product.getName())) {
                throw new RuntimeException("동일한 이름의 상품이 이미 존재합니다.");
            }
        }

        product.setId(ProductStore.sequence++);
        ProductStore.products.add(product);

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
    @PostMapping("/api/stock")
    public Map<String,Object> addStock(@RequestBody Map<String, Object> request){
        Long productId = ((Number) request.get("productId")).longValue();
        int quantity = ((Number) request.get("quantity")).intValue();

        if (quantity <= 0) {
            throw new RuntimeException("추가할 수량은 1 이상이어야 합니다.");
        }
        for (Product existingproduct : ProductStore.products) {

            if (existingproduct.getId().equals(productId)) {

                existingproduct.setStock(quantity + existingproduct.getStock());

                Map<String, Object> response = new HashMap<>();

                response.put("name", existingproduct.getName());
                response.put("stock", existingproduct.getStock());

                return response;
            }

        }

        throw new RuntimeException("상품을 찾을 수 없습니다.");
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
        List<Long> productIds = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Long productId = ((Number) item.get("productId")).longValue();
            boolean found = false;

            for (Product product : ProductStore.products) {
                if (product.getId().equals(productId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("존재하지 않는 상품입니다.");
            }
            productIds.add(productId);
        }
        ProductStore.products.removeIf(
                product -> productIds.contains(product.getId()));

        List<Map<String, Object>> remainingProducts = new ArrayList<>();

        for (Product product : ProductStore.products) {

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
