package gdg.hongik.mission.service;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.entity.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProductService {

    private final Repository repository;

    public ProductService(Repository repository) {
        this.repository = repository;
    }

    // 전체 상품 조회
    public List<Product> findAll() {
        return repository.findAll();
    }

    // ID로 상품 조회
    public Product findById(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID를 입력해주세요.");
        }

        return repository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "상품을 찾을 수 없습니다."
                        ));
    }

    // 이름으로 상품 하나 조회
    public Product findOneByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품명을 입력해주세요.");
        }

        List<Product> products = repository.findByName(name);

        if (products.isEmpty()) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }

        return products.get(0);
    }

    // 상품 등록
    public void createProduct(Product request) {
        if (request == null) {
            throw new IllegalArgumentException("상품 정보를 입력해주세요.");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("상품명을 입력해주세요.");
        }

        if (request.getPrice() <= 0 || request.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                    "가격과 초기 재고를 확인해주세요."
            );
        }

        if (!repository.findByName(request.getName()).isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 상품명입니다.");
        }

        Product product = new Product();

        // ID는 @GeneratedValue에 따라 자동 생성
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        repository.save(product);
    }

    // 재고 추가
    public Map<String, Object> addStock(
            Long productId,
            Integer quantityToAdd
    ) {
        if (quantityToAdd == null || quantityToAdd <= 0) {
            throw new IllegalArgumentException(
                    "추가 수량은 1 이상이어야 합니다."
            );
        }

        Product product = findById(productId);

        int finalStock = Math.addExact(
                product.getStockQuantity(),
                quantityToAdd
        );

        product.setStockQuantity(finalStock);

        Product savedProduct = repository.save(product);

        Map<String, Object> response = new HashMap<>();
        response.put("name", savedProduct.getName());
        response.put("stockQuantity", savedProduct.getStockQuantity());

        return response;
    }

    // 여러 상품 구매
    public Map<String, Object> purchaseProducts(
            List<Map<String, Long>> items
    ) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "구매할 상품을 입력해주세요."
            );
        }

        List<Product> selectedProducts = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        List<Map<String, Object>> purchasedItems = new ArrayList<>();

        Set<Long> selectedProductIds = new HashSet<>();

        long totalAmount = 0L;

        // 모든 상품과 구매 수량을 먼저 검증
        for (Map<String, Long> item : items) {
            if (item == null) {
                throw new IllegalArgumentException(
                        "구매 항목을 확인해주세요."
                );
            }

            Long productId = item.get("productId");
            Long quantity = item.get("quantity");

            if (productId == null || quantity == null
                    || quantity <= 0 || quantity > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "상품 ID와 구매 수량을 확인해주세요."
                );
            }

            if (!selectedProductIds.add(productId)) {
                throw new IllegalArgumentException(
                        "같은 상품은 하나의 항목으로 보내주세요."
                );
            }

            Product product = findById(productId);

            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            long amount = (long) product.getPrice() * quantity;

            Map<String, Object> purchasedItem = new HashMap<>();
            purchasedItem.put("name", product.getName());
            purchasedItem.put("quantity", quantity);
            purchasedItem.put("amount", amount);

            purchasedItems.add(purchasedItem);
            selectedProducts.add(product);
            quantities.add(quantity.intValue());

            totalAmount = Math.addExact(totalAmount, amount);
        }

        // 모든 검증이 끝난 뒤 재고 차감
        for (int i = 0; i < selectedProducts.size(); i++) {
            Product product = selectedProducts.get(i);

            product.setStockQuantity(
                    product.getStockQuantity() - quantities.get(i)
            );
        }

        repository.saveAll(selectedProducts);

        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount", totalAmount);
        response.put("items", purchasedItems);

        return response;
    }

    // 여러 상품 삭제
    public Map<String, Object> deleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException(
                    "삭제할 상품 ID를 입력해주세요."
            );
        }

        // 중복 ID는 한 번만 처리
        Set<Long> uniqueIds = new HashSet<>(ids);
        List<Product> productsToDelete = new ArrayList<>();

        // 삭제 전에 모든 상품의 존재 여부 확인
        for (Long id : uniqueIds) {
            productsToDelete.add(findById(id));
        }

        repository.deleteAll(productsToDelete);

        List<Map<String, Object>> remainingProducts =
                new ArrayList<>();

        for (Product product : repository.findAll()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", product.getName());
            item.put("stockQuantity", product.getStockQuantity());

            remainingProducts.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", remainingProducts);

        return response;
    }
}