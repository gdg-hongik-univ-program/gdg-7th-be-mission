package gdg.hongik.mission.service;

import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product.CreateProductRequest request){
        validateProduct(request);
        isDuplicatedProduct(request);

        Product product = new Product(request.id(), request.name(), request.price(), request.stock());
        productRepository.save(product);
        return product;
    }
    public  addStock(Long productId, Product.QuantityRequest request){
        validateQuantity(request.quantity());

        Product product = findProduct(productId);
        product.addStock(request.quantity());

        return
    }

    public void validateProduct(Product.CreateProductRequest request) {
        if (request.id() == null || request.id() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 ID는 1 이상이어야 합니다.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명은 비어 있을 수 없습니다.");
        }
        if (request.price() < 0 || request.stock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가격과 재고는 0 이상이어야 합니다.");
        }
    }
    public void isDuplicatedProduct(Product.CreateProductRequest request){
        if (productRepository.existsById(request.id()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다.");

        if (productRepository.existsByName(request.name()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복된 이름 입니다.");
    }


}
