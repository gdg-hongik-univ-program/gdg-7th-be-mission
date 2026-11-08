package gdg.hongik.mission.Service;

import gdg.hongik.mission.Entity.Product;
import gdg.hongik.mission.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // 구매 요청 한 건 (Controller에서 이 형태로 변환해서 넘깁니다)
    public record PurchaseItem(Long productId, long quantity) {
    }

    // ===================== 관리자 =====================

    // 상품 등록
    @Transactional
    public Map<String, Object> createProduct(String name, long price, long stock) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("상품명은 비어 있을 수 없습니다.");
        }
        if (price < 0 || stock < 0) {
            throw new RuntimeException("가격과 재고는 0 이상이어야 합니다.");
        }
        if (productRepository.existsByName(name)) {
            throw new RuntimeException("이미 존재하는 상품명입니다. (상품명: " + name + ")");
        }

        Product product = productRepository.save(new Product(name, price, stock));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("productId", product.getId());
        result.put("name", product.getName());
        result.put("price", product.getPrice());
        result.put("stock", product.getStock());
        return result;
    }

    // 재고 추가
    @Transactional
    public Map<String, Object> addStock(Long productId, long quantity) {
        if (quantity < 1) {
            throw new RuntimeException("추가할 수량은 1 이상이어야 합니다.");
        }

        Product product = findProductById(productId);
        product.setStock(product.getStock() + quantity); // 더티 체킹으로 자동 UPDATE

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", product.getName());
        result.put("stock", product.getStock());
        return result;
    }

    // 상품 삭제 (하나라도 없는 ID가 있으면 아무것도 삭제하지 않음)
    @Transactional
    public Map<String, Object> deleteProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new RuntimeException("삭제할 상품 ID가 없습니다.");
        }

        // 1단계: 존재 여부 검사
        List<Product> targets = new ArrayList<>();
        for (Long id : new HashSet<>(productIds)) {
            targets.add(findProductById(id));
        }

        // 2단계: 삭제
        productRepository.deleteAll(targets);

        // 남은 상품 목록
        List<Map<String, Object>> remaining = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", product.getName());
            item.put("stock", product.getStock());
            remaining.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("products", remaining);
        return result;
    }

    // ===================== 사용자 =====================

    // 상품 조회
    public Map<String, Object> getProduct(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다. (상품명: " + name + ")"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("productId", product.getId());
        result.put("name", product.getName());
        result.put("price", product.getPrice());
        result.put("stock", product.getStock());
        return result;
    }

    // 상품 구매 (하나라도 실패하면 아무것도 차감하지 않음)
    @Transactional
    public Map<String, Object> purchase(List<PurchaseItem> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("구매할 상품이 없습니다.");
        }

        // 1단계: 전부 구매 가능한지 먼저 검사
        Set<Long> seenIds = new HashSet<>();
        for (PurchaseItem item : items) {
            if (item.quantity() < 1) {
                throw new RuntimeException("구매 수량은 1 이상이어야 합니다.");
            }
            if (!seenIds.add(item.productId())) {
                throw new RuntimeException("같은 상품을 중복해서 요청할 수 없습니다. (상품 ID: " + item.productId() + ")");
            }

            Product product = findProductById(item.productId());
            if (product.getStock() < item.quantity()) {
                throw new RuntimeException("재고가 부족합니다. (상품: " + product.getName()
                        + ", 요청: " + item.quantity() + ", 재고: " + product.getStock() + ")");
            }
        }

        // 2단계: 재고 차감 + 응답 만들기
        long totalPrice = 0;
        List<Map<String, Object>> boughtItems = new ArrayList<>();
        for (PurchaseItem item : items) {
            Product product = findProductById(item.productId());
            product.setStock(product.getStock() - item.quantity());

            long price = product.getPrice() * item.quantity();
            totalPrice += price;

            Map<String, Object> bought = new LinkedHashMap<>();
            bought.put("name", product.getName());
            bought.put("quantity", item.quantity());
            bought.put("price", price);
            boughtItems.add(bought);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPrice", totalPrice);
        result.put("items", boughtItems);
        return result;
    }

    // ===================== 내부 공통 =====================

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다. (상품 ID: " + productId + ")"));
    }
}
