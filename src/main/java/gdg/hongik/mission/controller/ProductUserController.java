package gdg.hongik.mission.controller;

import gdg.hongik.mission.dto.CreateOrderRequest;
import gdg.hongik.mission.dto.CreateOrderResponse;
import gdg.hongik.mission.dto.GetProductResponse;
import gdg.hongik.mission.service.OrderService;
import gdg.hongik.mission.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // 모든 출처(프론트엔드)에서의 API 요청을 허용합니다.
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "소비자 상품 검색 및 구매", description = "소비자용 상품 조회 및 구매 API")
public class ProductUserController {

    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping(value = "/products", params = {"!id", "!itemName"})
    @Operation(summary = "전체 상품 조회",
            description = "카테고리를 포함한 전체 상품 목록을 반환합니다. 상품이 없으면 빈 배열을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "전체 상품 조회 성공")
    public ResponseEntity<List<GetProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping(value = "/products", params = {"itemName", "!id"})
    @Operation(summary = "상품명으로 상품 조회",
            description = "상품명이 일치하는 상품을 카테고리와 함께 반환하며, 상품이 없으면 404를 반환합니다.",
            parameters = @Parameter(name = "itemName", in = ParameterIn.QUERY,
                    required = true, description = "검색할 상품명", example = "키보드"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공 (단일 상품 객체)"),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    public ResponseEntity<GetProductResponse> getProductByName(
            @RequestParam("itemName") String name
    ) {
        GetProductResponse response = productService.getProductByName(name);

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
            @ApiResponse(responseCode = "400", description = "빈 요청 목록, null 항목, 상품 ID 누락 또는 0 이하, 구매 수량이 0 이하 (주문 전체 취소)"),
            @ApiResponse(responseCode = "404", description = "해당 상품 없음 (주문 전체 취소)"),
            @ApiResponse(responseCode = "409", description = "재고 부족 (주문 전체 취소)")
    })
    public ResponseEntity<CreateOrderResponse> orderProduct(
            @RequestBody
            @NotEmpty(message = "요청 목록은 비어 있을 수 없습니다.")
            @Valid List<@NotNull(message = "요청 항목은 null일 수 없습니다.") @Valid CreateOrderRequest> requests
    ) {

        CreateOrderResponse response = orderService.orderProduct(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
