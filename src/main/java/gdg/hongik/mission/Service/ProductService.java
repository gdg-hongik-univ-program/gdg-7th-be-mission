package gdg.hongik.mission.Service;


import gdg.hongik.mission.DTO.Request.ProductDelete;
import gdg.hongik.mission.DTO.Request.ProductPurchase;
import gdg.hongik.mission.DTO.Request.ProductRequest;
import gdg.hongik.mission.DTO.Response.ProductPruchaseResponse;
import gdg.hongik.mission.DTO.Response.PurchasedProduct;
import gdg.hongik.mission.Entity.Category;
import gdg.hongik.mission.Entity.Product;
import gdg.hongik.mission.Exception.DuplicateProductException;
import gdg.hongik.mission.Exception.InsufficientStockException;
import gdg.hongik.mission.Exception.ProductNotFoundException;
import gdg.hongik.mission.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }



    public List<Product> getAll(){
        return productRepository.findAll();
    }



    public List<Product> getProduct(String name) {
        Product product = productRepository.findByItemName(name)
                .orElseThrow(() -> new ProductNotFoundException("해당 이름의 상품이 없습니다."));

        return List.of(product);

    }

    @Transactional
    public ProductPruchaseResponse purchase(List<ProductPurchase> items) {


        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("구매할 상품이 없습니다.");
        }

        long totalPrice = 0;

        List<PurchasedProduct> itemlist = new ArrayList<>();

        for (ProductPurchase item : items) {

            Long productId = item.id();
            int quantity = item.count();

            if (quantity <= 0) {
                throw new IllegalArgumentException("구매 수량은 1 이상이어야 합니다.");
            }

            // 상품 조회

            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new RuntimeException("상품을 찾을 수 없습니다."));

            // 재고 검증

            if (product.getQuantity() < quantity) {
                throw new InsufficientStockException("재고가 부족합니다.");
            }

            // 재고 차감

            product.setQuantity(product.getQuantity() - quantity);

            // 구매 금액 계산

            long amount = (long) product.getPrice() * quantity;
            totalPrice += amount;

            // 구매 상품 정보 생성

            PurchasedProduct purchasedProduct = new PurchasedProduct(product.getItemName(),quantity, amount);
            itemlist.add(purchasedProduct);

        }
        ProductPruchaseResponse response = new ProductPruchaseResponse(totalPrice,itemlist);

        return response;

    }

    public Product createProduct(ProductRequest request){

        if (request.itemName() == null || request.itemName().isBlank()) {
            throw new RuntimeException("상품명을 입력해주세요.");
        }

        if (request.price() < 0 || request.quantity() < 0) {
            throw new RuntimeException("가격과 재고는 0 이상이어야 합니다.");
        }
        if(request.category() != Category.CLOTHES && request.category() != Category.COSMETICS && request.category() != Category.FOOD && request.category() != Category.ELECTRONICS)
            throw new RuntimeException("올바른 카테고리를 입력해주새요.");

        if(productRepository.existsByItemName(request.itemName())){
            throw new DuplicateProductException("동일한 이름의 상품이 이미 존재합니다.");
        }

        Product saveProduct = new Product(request.itemName(),request.price(),request.quantity(),request.category());
        return productRepository.save(saveProduct);
    }

    @Transactional
    public Product addStock(String productName, int quantity){
        if (quantity <= 0) {
            throw new RuntimeException("추가할 수량은 1 이상이어야 합니다.");
        }
        Product product = productRepository.findByItemName(productName)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        product.setQuantity(product.getQuantity() + quantity);

        return product;
    }

    @Transactional
    public List<Product> deleteProducts(ProductDelete items) {

        String productName = items.itemName();
        Product product = productRepository.findByItemName(productName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));
        productRepository.delete(product);

        return productRepository.findAll();
    }

}
