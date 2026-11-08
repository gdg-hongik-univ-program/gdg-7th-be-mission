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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "소비자 상품", description = "소비자용 상품 조회 및 구매 API")
public class ProductUserController {

    private final ProductService productService;

    @GetMapping("/products")
    @Operation(summary = "상품 조회",
            description = "id 또는 name 중 하나만 입력하여 조회합니다. 상품명은 정확히 일치해야 하며, 상품이 없으면 404를 반환합니다.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.QUERY,
                            description = "상품 ID (1 이상, name과 동시 사용 불가)", example = "1"),
                    @Parameter(name = "name", in = ParameterIn.QUERY,
                            description = "상품명 (공백 불가, id와 동시 사용 불가)", example = "키보드")
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공 (단일 상품 객체)"),
            @ApiResponse(responseCode = "400", description = "조회 조건 누락, 동시 입력 또는 잘못된 값"),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    public ResponseEntity<Product.GetProductResponse> getProduct(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name
    ) {

        Product.GetProductResponse response = productService.getProduct(id, name);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders")
    @Operation(summary = "여러 상품 구매",
            description = "상품 ID와 수량을 객체 배열로 전달합니다. 수량은 1 이상이며, "
                    + "요청한 순서대로 상품 존재 여부, 수량, 재고를 검증하고 재고를 차감합니다. "
                    + "동일 상품이 여러 번 요청되면 앞선 요청에서 차감된 재고를 기준으로 검증합니다. "
                    + "하나의 트랜잭션으로 일괄 구매 처리하며, 검증 또는 처리 중 오류가 발생하면 "
                    + "앞서 차감한 재고를 포함한 전체 변경을 롤백하고 오류를 반환합니다. "
                    + "모든 상품 구매에 성공하면 구매 상품 목록과 합계를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "모든 상품 구매 성공"),
            @ApiResponse(responseCode = "400", description = "수량이 0 이하 (주문 전체 취소)"),
            @ApiResponse(responseCode = "404", description = "해당 상품 없음 (주문 전체 취소)"),
            @ApiResponse(responseCode = "409", description = "재고 부족 (주문 전체 취소)")
    })
    public ResponseEntity<Product.CreateOrderResponse> orderProduct(@RequestBody List<Product.CreateOrderRequest> requests) {

        Product.CreateOrderResponse response = productService.orderProduct(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
