package gdg.hongik.mission.Service;


import gdg.hongik.mission.DTO.*;
import gdg.hongik.mission.Entity.Product;
import gdg.hongik.mission.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Product getProduct(String name){
        return productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("해당 이름의 상품이 없습니다."));
    }

    @Transactional
    public ProductPruchaseResponse purchase(List<ProductPurchase> items) {


        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("구매할 상품이 없습니다.");
        }

        long totalPrice = 0;

        ProductPruchaseResponse response = new ProductPruchaseResponse();

        for (ProductPurchase item : items) {

            Long productId = item.id();
            int quantity = item.quantity();

            if (quantity <= 0) {
                throw new IllegalArgumentException("구매 수량은 1 이상이어야 합니다.");
            }

            // 상품 조회

            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new RuntimeException("상품을 찾을 수 없습니다."));

            // 재고 검증

            if (product.getStock() < quantity) {
                throw new RuntimeException("재고가 부족합니다.");
            }

            // 재고 차감

            product.setStock(product.getStock() - quantity);

            // 구매 금액 계산

            long amount = (long) product.getPrice() * quantity;
            totalPrice += amount;

            // 구매 상품 정보 생성

            PurchasedProduct purchasedProduct = new PurchasedProduct(product.getName(),quantity,amount);
            response.products().add(purchasedProduct);

        }

        return response;

    }

    public Product createProduct(ProductRequest request){

        if (request.name() == null || request.name().isBlank()) {
            throw new RuntimeException("상품명을 입력해주세요.");
        }

        if (request.price() < 0 || request.stock() < 0) {
            throw new RuntimeException("가격과 재고는 0 이상이어야 합니다.");
        }

        if(productRepository.existsByName(request.name())){
            throw new RuntimeException("동일한 이름의 상품이 이미 존재합니다.");
        }

        Product saveProduct = new Product(request.name(),request.price(),request.stock());
        return productRepository.save(saveProduct);
    }

    @Transactional
    public Product addStock(Long productId, int quantity){
        if (quantity <= 0) {
            throw new RuntimeException("추가할 수량은 1 이상이어야 합니다.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        product.setStock(product.getStock() + quantity);

        return product;
    }

    @Transactional
    public List<Product> deleteProducts(List<ProductDelete> items){

        for(ProductDelete item : items){
            
            Long productId = item.id();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));
            productRepository.delete(product);
        }
        return productRepository.findAll();
    }

}
