package gdg.hongik.mission.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdg.hongik.mission.dto.request.ProductCreateRequest;
import gdg.hongik.mission.dto.request.ProductDeleteRequest;
import gdg.hongik.mission.dto.request.PurchaseItemRequest;
import gdg.hongik.mission.dto.request.StockUpdateRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.dto.response.PurchaseResponse;
import gdg.hongik.mission.dto.response.PurchasedProductResponse;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // 1-1. 상품 조회
    public ProductResponse searchProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("해당 이름의 상품을 찾을 수 없습니다."));

        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity());
    }

    // 1-2. 상품 구매 (List<PurchaseItemRequest> 직접 처리)
    @Transactional
    public PurchaseResponse orderProducts(List<PurchaseItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("구매할 상품 목록이 비어있습니다.");
        }

        // STEP 1: 사전 검증
        for (PurchaseItemRequest item : items) {
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
        List<PurchasedProductResponse> purchasedItemResponses = new ArrayList<>();

        for (PurchaseItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId()).get();

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            int subtotal = product.getPrice() * item.getQuantity();
            totalAmount += subtotal;

            purchasedItemResponses.add(new PurchasedProductResponse(
                    product.getName(),
                    item.getQuantity(),
                    subtotal));
        }

        return new PurchaseResponse(totalAmount, purchasedItemResponses);
    }

    // 2-1. 상품 등록
    @Transactional
    public ProductResponse registerProduct(ProductCreateRequest request) {
        if (request.getPrice() < 0 || request.getStockQuantity() < 0) {
            throw new RuntimeException("가격과 재고는 0 이상이어야 합니다.");
        }
        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("이미 존재하는 상품명입니다: " + request.getName());
        }

        Product newProduct = new Product(request.getName(), request.getPrice(), request.getStockQuantity());
        productRepository.save(newProduct);

        return new ProductResponse(
                newProduct.getProductId(),
                newProduct.getName(),
                newProduct.getPrice(),
                newProduct.getStockQuantity());
    }

    // 2-2. 재고 추가
    @Transactional
    public ProductResponse addStock(StockUpdateRequest request) {
        if (request.getQuantityToAdd() <= 0) {
            throw new RuntimeException("추가할 재고 수량은 1개 이상이어야 합니다.");
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("재고를 추가할 상품(ID: " + request.getProductId() + ")이 존재하지 않습니다."));

        product.setStockQuantity(product.getStockQuantity() + request.getQuantityToAdd());
        productRepository.save(product);

        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity());
    }

    // 2-3. 상품 삭제
    @Transactional
    public List<ProductResponse> deleteProducts(ProductDeleteRequest request) {
        List<Long> productIds = request.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            throw new RuntimeException("삭제할 상품 ID 목록이 비어있습니다.");
        }

        for (Long id : productIds) {
            if (!productRepository.existsById(id)) {
                throw new RuntimeException("삭제하려는 상품 중 일부 또는 전체가 존재하지 않습니다. (ID: " + id + ")");
            }
        }

        productRepository.deleteAllById(productIds);

        return productRepository.findAll().stream()
                .map(product -> new ProductResponse(
                        product.getProductId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStockQuantity()))
                .collect(Collectors.toList());
    }
}