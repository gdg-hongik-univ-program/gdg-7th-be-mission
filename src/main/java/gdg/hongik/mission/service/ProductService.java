package gdg.hongik.mission.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gdg.hongik.mission.controller.ProductUserController.OrderItem;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 만들어 의존성 주입(DI)을 처리
public class ProductService {

    private final ProductRepository productRepository;

    // 1-1. 상품 조회
    public Map<String, Object> searchProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("해당 이름의 상품을 찾을 수 없습니다."));

        // 엔티티 직접 반환 대신 Map으로 필요한 정보만 포장
        return Map.of(
                "productId", product.getProductId(),
                "name", product.getName(),
                "price", product.getPrice(),
                "stockQuantity", product.getStockQuantity());
    }

    // 1-2. 상품 구매
    public Map<String, Object> orderProducts(List<OrderItem> items) {
        // STEP 1: 사전 검증
        for (OrderItem item : items) {
            if (item.getQuantity() <= 0) {
                throw new RuntimeException("구매 수량은 1개 이상이어야 합니다.");
            }
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("요청한 상품(ID: " + item.getProductId() + ")이 존재하지 않습니다."));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException(product.getName() + " 상품의 재고가 부족합니다.");
            }
        }

        // STEP 2: 재고 차감 및 응답 생성
        int totalAmount = 0;
        List<Map<String, Object>> purchasedItems = new ArrayList<>();

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).get();

            // 재고 차감 및 DB 저장 (Week 2 방식: 더티 체킹 없이 명시적 save 호출)
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            int subtotal = product.getPrice() * item.getQuantity();
            totalAmount += subtotal;

            purchasedItems.add(Map.of(
                    "productName", product.getName(),
                    "quantity", item.getQuantity(),
                    "subtotalAmount", subtotal));
        }

        return Map.of("totalAmount", totalAmount, "purchasedItems", purchasedItems);
    }

    // 2-1. 상품 등록
    public Map<String, Object> registerProduct(String name, int price, int stockQuantity) {
        if (price < 0 || stockQuantity < 0) {
            throw new RuntimeException("가격과 재고는 0 이상이어야 합니다.");
        }
        if (productRepository.existsByName(name)) {
            throw new RuntimeException("이미 존재하는 상품명입니다: " + name);
        }

        Product newProduct = new Product(name, price, stockQuantity);
        productRepository.save(newProduct);

        // 등록된 엔티티의 정보를 Map으로 포장하여 반환
        return Map.of(
                "productId", newProduct.getProductId(), // DB에 저장되며 생성된 ID 포함
                "name", newProduct.getName(),
                "price", newProduct.getPrice(),
                "stockQuantity", newProduct.getStockQuantity());
    }

    // 2-2. 재고 추가
    public Map<String, Object> addStock(Long productId, int quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new RuntimeException("추가할 재고 수량은 1개 이상이어야 합니다.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("재고를 추가할 상품(ID: " + productId + ")이 존재하지 않습니다."));

        product.setStockQuantity(product.getStockQuantity() + quantityToAdd);
        productRepository.save(product);

        return Map.of(
                "productName", product.getName(),
                "finalStockQuantity", product.getStockQuantity());
    }

    // 2-3. 상품 삭제
    public Map<String, Object> deleteProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new RuntimeException("삭제할 상품 ID 목록이 비어있습니다.");
        }

        // 상품 존재 여부 검증
        for (Long id : productIds) {
            if (!productRepository.existsById(id)) {
                throw new RuntimeException("삭제하려는 상품 중 일부 또는 전체가 존재하지 않습니다. (ID: " + id + ")");
            }
        }

        // 삭제 처리
        productRepository.deleteAllById(productIds);

        // 남은 상품 응답
        List<Map<String, Object>> remainingProducts = productRepository.findAll().stream()
                .map(p -> Map.<String, Object>of(
                        "productName", p.getName(),
                        "stockQuantity", p.getStockQuantity()))
                .collect(Collectors.toList());

        return Map.of("remainingProducts", remainingProducts);
    }
}