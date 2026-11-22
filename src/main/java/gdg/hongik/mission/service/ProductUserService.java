package gdg.hongik.mission.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdg.hongik.mission.dto.request.PurchaseItemRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.dto.response.PurchaseResponse;
import gdg.hongik.mission.dto.response.PurchasedProductResponse;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.exception.BusinessException;
import gdg.hongik.mission.exception.ErrorCode;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductUserService {

    private final ProductRepository productRepository;

    // 1-1. 전체 상품 목록 조회
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    // 1-2. 상품명 단건 검색
    public ProductResponse searchProductByName(String itemName) {
        Product product = productRepository.findByName(itemName)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "해당 이름의 상품을 찾을 수 없습니다: " + itemName));

        return ProductResponse.from(product);
    }

    // 1-3. 장바구니 상품 일괄 구매
    @Transactional
    public PurchaseResponse orderProducts(List<PurchaseItemRequest> items) {
        // 1) 동일 상품 ID 중복 요청 시 수량 합산
        Map<Long, Integer> countByProductId = new LinkedHashMap<>();
        for (PurchaseItemRequest item : items) {
            countByProductId.merge(item.id(), item.count(), Integer::sum);
        }

        // 2) 요청 상품 일괄 조회 (단건 반복 조회 및 이중 조회 제거)
        Map<Long, Product> productMap = productRepository.findAllById(countByProductId.keySet()).stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        // 3) 재고 차감 및 응답 생성
        int totalPrice = 0;
        List<PurchasedProductResponse> purchasedItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : countByProductId.entrySet()) {
            Long productId = entry.getKey();
            int count = entry.getValue();

            Product product = productMap.get(productId);
            if (product == null) {
                throw new BusinessException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "요청한 상품(ID: " + productId + ")이 존재하지 않습니다.");
            }

            product.decreaseStock(count);

            PurchasedProductResponse itemResponse = PurchasedProductResponse.of(product, count);
            totalPrice += itemResponse.price();
            purchasedItems.add(itemResponse);
        }

        return new PurchaseResponse(totalPrice, purchasedItems);
    }
}
