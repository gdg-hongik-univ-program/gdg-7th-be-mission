package gdg.hongik.mission.controller;

import gdg.hongik.mission.Product;
import gdg.hongik.mission.ProductStore;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 소비자(User)용 상품 조회 및 구매 API 요청을 처리하는 컨트롤러입니다.
 * - @RequestMapping("/products"): 이 컨트롤러의 모든 API 경로 앞에 공통으로 "/products"가 붙습니다.
 */
@RestController
@RequestMapping("/products")
public class ProductUserController {

    /**
     * 1. 상품 조회 API (GET /products?name=상품명)
     * - GET 요청은 본문(Body) 대신 URL의 쿼리 파라미터(?name=키보드)로 데이터를 전달받습니다.
     * - @RequestParam: URL 쿼리 파라미터의 "name" 값을 String 변수 name에 담아줍니다.
     */
    @GetMapping
    public Product getProductByName(@RequestParam String name) {
        // 저장소에 있는 상품들을 순회하며 요청받은 상품명(name)과 이름이 같은 상품을 찾아 반환합니다.
        for (Product product : ProductStore.products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }

        // 일치하는 상품을 찾지 못한 경우 예외를 발생시킵니다.
        throw new RuntimeException("상품을 찾을 수 없습니다.");
    }

    /**
     * 2. 상품 구매 API (POST /products/purchase)
     * 여러 상품을 한 번에 구매할 수 있도록 List<Product> 형태로 요청 바디를 받습니다.
     * - 요청의 각 Product 객체에는 구매할 상품의 ID(id)와 구매 수량(stock)이 담겨 있습니다.
     */
    @PostMapping("/purchase")
    public Map<String, Object> purchaseProducts(@RequestBody List<Product> requests) {
        int totalPrice = 0; // 전체 구매 금액을 누적할 변수
        List<Product> purchasedProducts = new ArrayList<>(); // 구매 완료된 상품 내역을 담을 리스트

        // 1) 클라이언트가 구매 요청한 항목들을 하나씩 꺼냅니다.
        for (Product request : requests) {
            Product targetProduct = null;

            // 2) 저장소에서 구매하려는 상품 ID와 일치하는 실제 상품을 찾습니다.
            for (Product product : ProductStore.products) {
                if (product.getId().equals(request.getId())) {
                    targetProduct = product;
                    break;
                }
            }

            // 상품을 찾지 못한 경우 예외를 발생시킵니다.
            if (targetProduct == null) {
                throw new RuntimeException("상품을 찾을 수 없습니다.");
            }

            // 재고가 구매 수량보다 부족한 경우 예외를 발생시킵니다.
            if (targetProduct.getStock() < request.getStock()) {
                throw new RuntimeException("재고가 부족합니다.");
            }

            // 실제 상품의 재고에서 구매 수량(request.getStock())만큼 차감합니다.
            targetProduct.setStock(targetProduct.getStock() - request.getStock());

            // 해당 상품의 소비 금액(단가 * 구매 수량)을 계산하고 총 구매 금액에 더합니다.
            int itemTotalPrice = targetProduct.getPrice() * request.getStock();
            totalPrice += itemTotalPrice;

            // 구매한 상품 목록에 추가합니다.
            purchasedProducts.add(new Product(
                    targetProduct.getId(),
                    targetProduct.getName(),
                    itemTotalPrice,
                    request.getStock()
            ));
        }

        // 총 구매 금액(totalPrice)과 구매한 상품 목록(purchasedProducts)을 Map에 담아 JSON 형태로 반환합니다.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalPrice", totalPrice);
        response.put("purchasedProducts", purchasedProducts);
        return response;
    }
}
