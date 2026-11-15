package gdg.hongik.mission.service;

import gdg.hongik.mission.dto.ProductRequest;
import gdg.hongik.mission.dto.ProductResponse;
import gdg.hongik.mission.dto.PurchaseRequest;
import gdg.hongik.mission.dto.PurchaseResponse;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.entity.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final Repository repository;

    public ProductService(Repository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> findAllProducts() {
        return repository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findProductByItemName(String itemName) {
        return ProductResponse.from(findOneByItemName(itemName));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("상품 정보를 입력해주세요.");
        }

        String itemName = request.name();

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("상품명을 입력해주세요.");
        }

        if (request.category() == null) {
            throw new IllegalArgumentException("카테고리를 입력해주세요.");
        }

        if (request.price() <= 0 || request.stockQuantity() < 0) {
            throw new IllegalArgumentException("가격과 초기 재고를 확인해주세요.");
        }

        if (!repository.findByItemName(itemName).isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 상품명입니다.");
        }

        Product product = new Product();
        product.setItemName(itemName);
        product.setPrice(request.price());
        product.setQuantity(request.stockQuantity());
        product.setCategory(request.category());

        return ProductResponse.from(repository.save(product));
    }

    @Transactional
    public ProductResponse addStock(String itemName, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("추가 수량은 1 이상이어야 합니다.");
        }

        Product product = findOneByItemName(itemName);
        int updatedQuantity = Math.addExact(product.getQuantity(), quantity);
        product.setQuantity(updatedQuantity);

        return ProductResponse.from(product);
    }

    @Transactional
    public PurchaseResponse purchaseProducts(List<PurchaseRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("구매할 상품을 입력해주세요.");
        }

        Set<Long> selectedIds = new HashSet<>();
        List<PurchaseResponse.PurchaseItemResponse> purchasedItems = new ArrayList<>();
        long totalPrice = 0L;

        for (PurchaseRequest item : items) {
            if (item == null || item.id() == null
                    || item.count() == null || item.count() <= 0) {
                throw new IllegalArgumentException("상품 ID와 구매 수량을 확인해주세요.");
            }

            if (!selectedIds.add(item.id())) {
                throw new IllegalArgumentException("같은 상품은 하나의 항목으로 보내주세요.");
            }

            Product product = repository.findById(item.id())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            int count = item.count();

            if (product.getQuantity() < count) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            long itemPrice = (long) product.getPrice() * count;
            totalPrice = Math.addExact(totalPrice, itemPrice);
            product.setQuantity(product.getQuantity() - count);

            purchasedItems.add(new PurchaseResponse.PurchaseItemResponse(
                    product.getItemName(),
                    count,
                    itemPrice
            ));
        }

        return new PurchaseResponse(totalPrice, purchasedItems);
    }

    @Transactional
    public List<ProductResponse> deleteProduct(String itemName) {
        Product product = findOneByItemName(itemName);
        repository.delete(product);

        return findAllProducts();
    }

    private Product findOneByItemName(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("상품명을 입력해주세요.");
        }

        return repository.findByItemName(itemName).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }
}