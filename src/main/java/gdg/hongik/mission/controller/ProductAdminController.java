package gdg.hongik.mission.controller;

import gdg.hongik.mission.Product;
import gdg.hongik.mission.ProductStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자(Admin)용 상품 관리 API 요청을 처리하는 컨트롤러입니다.
 * - @RestController: 이 클래스가 컨트롤러임을 선언하며, 메서드의 반환값을 자동으로 JSON 형태로 응답합니다.
 * - @RequestMapping("/admin/products"): 이 컨트롤러의 모든 API 경로 앞에 공통으로 "/admin/products"가 붙습니다.
 */
@RestController
@RequestMapping("/admin/products")
public class ProductAdminController {

    /**
     * 1. 상품 등록 API (POST /admin/products)
     * 클라이언트가 보낸 JSON(상품명, 가격, 초기 재고)을 @RequestBody를 통해 Product 객체로 변환하여 받습니다.
     */
    @PostMapping
    public Product registerProduct(@RequestBody Product request) {
        // 동일한 이름의 상품이 이미 존재하는지 확인하고, 존재하면 예외를 발생시킵니다.
        for (Product product : ProductStore.products) {
            if (product.getName().equals(request.getName())) {
                throw new RuntimeException("이미 존재하는 상품입니다.");
            }
        }

        // sequence 값을 사용해 고유 ID를 부여하고, 그다음 상품을 위해 sequence를 1 증가시킵니다(sequence++).
        Product newProduct = new Product(
                ProductStore.sequence++,
                request.getName(),
                request.getPrice(),
                request.getStock()
        );

        // 메모리 저장소(ProductStore)의 상품 리스트에 새 상품을 저장합니다.
        ProductStore.products.add(newProduct);

        // 등록된 상품 정보를 클라이언트에게 JSON으로 반환합니다.
        return newProduct;
    }

    /**
     * 2. 재고 추가 API (PATCH /admin/products/stock)
     * 기존 상품의 일부 정보(재고 수량)만 수정하므로 PATCH 메서드를 사용합니다.
     * 요청 바디로 상품 ID(id)와 추가할 수량(stock)을 전달받습니다.
     */
    @PatchMapping("/stock")
    public Product addStock(@RequestBody Product request) {
        // 저장소에 있는 전체 상품을 하나씩 확인하며 요청받은 ID와 일치하는 상품을 찾습니다.
        for (Product product : ProductStore.products) {
            if (product.getId().equals(request.getId())) {
                // 기존 재고(product.getStock())에 추가할 수량(request.getStock())을 더해서 저장합니다.
                product.setStock(product.getStock() + request.getStock());
                return product;
            }
        }

        // 반복문이 끝날 때까지 상품을 찾지 못한 경우 예외를 발생시킵니다.
        throw new RuntimeException("상품을 찾을 수 없습니다.");
    }

    /**
     * 3. 상품 삭제 API (DELETE /admin/products)
     * 삭제할 상품 ID들의 목록(예: [1, 2])을 JSON 배열 형태로 전달받아 한 번에 삭제합니다.
     */
    @DeleteMapping
    public List<Product> deleteProducts(@RequestBody List<Long> productIds) {
        // 1) 삭제 요청받은 상품 ID들을 하나씩 꺼냅니다.
        for (Long id : productIds) {
            boolean found = false;

            // 2) 현재 저장된 상품 목록을 순회하며 해당 ID를 가진 상품을 찾습니다.
            for (Product product : ProductStore.products) {
                if (product.getId().equals(id)) {
                    // 일치하는 상품을 찾으면 리스트에서 삭제합니다.
                    ProductStore.products.remove(product);
                    found = true;
                    // 삭제 후에는 안쪽 반복문을 바로 종료(break)하고 다음 삭제할 ID로 넘어갑니다.
                    break;
                }
            }

            // 삭제하려는 ID의 상품이 존재하지 않는 경우 예외를 발생시킵니다.
            if (!found) {
                throw new RuntimeException("삭제할 상품을 찾을 수 없습니다.");
            }
        }

        // 삭제 후 남아있는 전체 상품 목록을 반환합니다.
        return ProductStore.products;
    }
}
