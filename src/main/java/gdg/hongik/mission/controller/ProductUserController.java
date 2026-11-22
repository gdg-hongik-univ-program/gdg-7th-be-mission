package gdg.hongik.mission.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gdg.hongik.mission.dto.request.PurchaseItemRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.dto.response.PurchaseResponse;
import gdg.hongik.mission.service.ProductUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductUserController {

    private final ProductUserService productUserService;

    // 1-1. 전체 상품 목록 조회
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productUserService.getAllProducts();
    }

    // 1-2. 상품명 단건 검색
    @GetMapping(params = "itemName")
    public ProductResponse searchProduct(@RequestParam @NotBlank(message = "검색할 상품명을 입력해주세요.") String itemName) {
        return productUserService.searchProductByName(itemName);
    }

    // 1-3. 장바구니 상품 일괄 구매
    @PostMapping("/purchase")
    public PurchaseResponse orderProducts(
            @RequestBody @NotEmpty(message = "구매할 상품 목록이 비어있습니다.") @Valid List<PurchaseItemRequest> items) {
        return productUserService.orderProducts(items);
    }
}