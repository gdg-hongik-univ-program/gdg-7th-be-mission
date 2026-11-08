package gdg.hongik.mission.service;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product.GetProductResponse getProduct(String name){

        Product result = productRepository.findByName(name)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

        return new Product.GetProductResponse(result.getId(), result.getName(), result.getPrice(), result.getStock());
    }

    public Product.CreateOrderResponse orderProduct(List<Product.OrderRequest> requestList){

        for (PurchaseRequest request : requests) {
            Long productId = request.productId();
            int quantity = request.quantity();
        }
        if (requestList.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"구매 수량은 1 이상이어야 합니다.");
        }

        Product product = productStore.getProducts().stream()
                .filter(savedProduct -> savedProduct.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        if (product.getStock() < request.quantity()) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        product.removeStock(request.quantity());

        PurchaseResponse response = new PurchaseResponse(
                product.getId(),
                product.getName(),
                request.quantity(),
                product.getPrice() * request.quantity(),
                product.getStock()
        );
    }
}
