package gdg.hongik.mission.controller;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.ProductStore;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "소비자 상품", description = "소비자용 상품 조회 및 구매 API")
public class ProductUserController {

    private final ProductService productService;

    @GetMapping("/products")
    @Operation(summary = "상품명으로 상품 조회",
            description = "상품명으로 검색합니다. 검색 결과가 없으면 빈 배열을 반환합니다.",
            parameters = @Parameter(name = "name", in = ParameterIn.QUERY, required = true,
                    description = "검색할 상품명", example = "키보드"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 결과 배열 (결과가 없으면 빈 배열)"),
            @ApiResponse(responseCode = "400", description = "검색어 누락 또는 빈 문자열")
    })
    public ResponseEntity<Product.GetProductResponse> getProduct(String name) {

        Product.GetProductResponse response = productService.getProduct(name);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders")
    @Operation(summary = "여러 상품 구매",
            description = "상품 ID와 수량을 객체 배열로 전달합니다. 수량은 1 이상이며, "
                    + "상품이 없거나 수량이 잘못되거나 재고가 부족하면 해당 상품은 건너뜁니다. 성공한 상품 목록과 합계를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "한 항목 이상 구매 성공 (부분 성공 포함)"),
            @ApiResponse(responseCode = "200", description = "모두 실패하면 빈 items와 totalPrice 0")
    })
    public ResponseEntity<Product.CreateOrderResponse> orderProduct(@RequestBody List<Product.OrderRequest> requestList) {

        CreateOrderResponse response = productService.orderProduct(requestList);

        return ResponseEntity.ok(response);
    }
}
