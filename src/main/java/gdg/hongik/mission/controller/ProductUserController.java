package gdg.hongik.mission.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gdg.hongik.mission.dto.request.PurchaseItemRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.dto.response.PurchaseResponse;
import gdg.hongik.mission.service.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductUserController {

    private final ProductService productService;

    // 1-1. 상품 조회
    @GetMapping("/products/search")
    public ProductResponse searchProduct(@RequestParam String name) {
        return productService.searchProductByName(name);
    }

    // 1-2. 상품 구매 (List<PurchaseItemRequest> 바로 수신)
    @PostMapping("/orders")
    public PurchaseResponse orderProducts(@RequestBody List<PurchaseItemRequest> items) {
        return productService.orderProducts(items);
    }
}