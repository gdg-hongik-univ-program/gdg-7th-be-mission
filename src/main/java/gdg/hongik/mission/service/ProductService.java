package gdg.hongik.mission.service;

import gdg.hongik.mission.dto.request.ProductCreateRequest;
import gdg.hongik.mission.dto.request.ProductDeleteRequest;
import gdg.hongik.mission.dto.request.PurchaseItemRequest;
import gdg.hongik.mission.dto.request.StockUpdateRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.dto.response.PurchaseResponse;
import gdg.hongik.mission.dto.response.PurchasedProductResponse;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.exception.DuplicateProductNameException;
import gdg.hongik.mission.exception.ProductNotFoundException;
import gdg.hongik.mission.repository.ProductRepository;
import gdg.hongik.mission.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // ===================== 소비자 =====================

    // 1.1 전체 상품 목록 조회
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    // 1.2 상품명 단건 조회
    public ProductResponse getProduct(String itemName) {
        return ProductResponse.from(findByItemName(ProductValidator.itemName(itemName)));
    }

    // 1.3 장바구니 일괄 구매 (하나라도 실패하면 전부 롤백)
    @Transactional
    public PurchaseResponse purchase(List<PurchaseItemRequest> requests) {
        ProductValidator.purchase(requests); // 검증

        long totalPrice = 0;
        List<PurchasedProductResponse> items = new ArrayList<>();
        for (PurchaseItemRequest request : requests) {
            Product product = productRepository.findById(request.id())
                    .orElseThrow(ProductNotFoundException::new);
            product.decreaseQuantity(request.count()); // 재고 부족이면 예외 → 롤백

            long price = Math.multiplyExact(product.getPrice(), request.count());
            totalPrice = Math.addExact(totalPrice, price);
            items.add(new PurchasedProductResponse(product.getItemName(), request.count(), price));
        }
        return new PurchaseResponse(totalPrice, items);
    }

    // ===================== 관리자 =====================

    // 2.1 상품 등록
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        ProductValidator.create(request); // 검증

        String itemName = ProductValidator.itemName(request.itemName());
        if (productRepository.existsByItemName(itemName)) {
            throw new DuplicateProductNameException();
        }
        return ProductResponse.from(productRepository.save(
                new Product(itemName, request.price(), request.quantity(), request.category())));
    }

    // 2.2 재고 추가
    @Transactional
    public ProductResponse addStock(StockUpdateRequest request) {
        ProductValidator.stock(request); // 검증

        Product product = findByItemName(ProductValidator.itemName(request.itemName()));
        product.addQuantity(request.quantity());
        return ProductResponse.from(product);
    }

    // 2.3 상품 삭제 (삭제 후 남은 전체 목록 반환)
    @Transactional
    public List<ProductResponse> deleteProduct(ProductDeleteRequest request) {
        ProductValidator.delete(request); // 검증

        productRepository.delete(findByItemName(ProductValidator.itemName(request.itemName())));
        return getAllProducts();
    }

    private Product findByItemName(String itemName) {
        return productRepository.findByItemName(itemName)
                .orElseThrow(ProductNotFoundException::new);
    }
}