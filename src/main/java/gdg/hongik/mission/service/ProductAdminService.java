package gdg.hongik.mission.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdg.hongik.mission.dto.request.ProductCreateRequest;
import gdg.hongik.mission.dto.request.ProductDeleteRequest;
import gdg.hongik.mission.dto.request.StockUpdateRequest;
import gdg.hongik.mission.dto.response.ProductResponse;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.exception.BusinessException;
import gdg.hongik.mission.exception.ErrorCode;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAdminService {

    private final ProductRepository productRepository;

    // 2-1. 상품 등록
    @Transactional
    public ProductResponse registerProduct(ProductCreateRequest request) {
        if (productRepository.existsByName(request.itemName())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_PRODUCT_NAME,
                    "이미 존재하는 상품명입니다: " + request.itemName());
        }

        Product newProduct = new Product(
                request.itemName(),
                request.price(),
                request.quantity(),
                request.category());
        productRepository.save(newProduct);

        return ProductResponse.from(newProduct);
    }

    // 2-2. 재고 추가 (상품명 기준)
    @Transactional
    public ProductResponse addStock(StockUpdateRequest request) {
        Product product = productRepository.findByName(request.itemName())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "재고를 추가할 상품(" + request.itemName() + ")이 존재하지 않습니다."));

        product.increaseStock(request.quantity());

        return ProductResponse.from(product);
    }

    // 2-3. 상품 삭제 (상품명 기준)
    @Transactional
    public List<ProductResponse> deleteProduct(ProductDeleteRequest request) {
        Product product = productRepository.findByName(request.itemName())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "삭제하려는 상품(" + request.itemName() + ")이 존재하지 않습니다."));

        productRepository.delete(product);

        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }
}
