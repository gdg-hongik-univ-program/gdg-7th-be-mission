package gdg.hongik.mission.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import gdg.hongik.mission.entity.Product;
import gdg.hongik.mission.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    //상품 조회
    public Product.GetProductResponse getProduct(Long id, String name){

        if ((id == null && name == null)
                || (id != null && name != null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "id 또는 name 중 하나만 입력해 주세요."
            );
        }

        Product result;

        if (id != null) {
            if (id <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 ID는 1 이상이어야 합니다.");
            }
            result = productRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "상품을 찾을 수 없습니다."
                    ));
        }
        else {
            if (name.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명을 입력해 주세요.");
            }
            result = productRepository.findByName(name)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "상품을 찾을 수 없습니다."
                    ));
        }

        return new Product.GetProductResponse(result.getId(), result.getName(), result.getPrice(), result.getStock());
    }

    //상품 구매
    @Transactional
    public Product.CreateOrderResponse orderProduct(List<Product.CreateOrderRequest> requests){
        //요청 검증
        validateRequests(requests);

        int totalPrice = 0;
        List<Product.OrderedItems> items = new ArrayList<>();

        for(Product.CreateOrderRequest request : requests){
           
            if (request.productId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 ID는 비어있을 수 없습니다.");
            }
            //상품 검증
            Product targetProduct = productRepository.findById(request.productId())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

            //재고 수량 검증
            validateOrderedQuantity(targetProduct, request);
            //재고 차감
            targetProduct.deductStock(request.quantity());
            //items 에 추가
            int targetPrice = request.quantity() * targetProduct.getPrice();
            items.add(new Product.OrderedItems(
                    targetProduct.getId(),
                    targetProduct.getName(),
                    request.quantity(),
                    targetPrice,
                    targetProduct.getStock()
            ));
            //가격 계산
            totalPrice += targetPrice;
        }

        return new Product.CreateOrderResponse(items, totalPrice);
    }


    //상품 생성
    @Transactional
    public List<Product.CreateProductResponse> createProduct(List<Product.CreateProductRequest> requests){

        //요청 검증
        validateRequests(requests);

        //재고 수, 이름 공란, 가격 검증
        validateProduct(requests);
        //이름 중복 검증
        isDuplicatedProduct(requests);

        //통과 후 생성
        List<Product.CreateProductResponse> responses = new ArrayList<>();

        for (Product.CreateProductRequest request : requests){

            Product product = new Product(request.name(), request.price(), request.stock());
            Product savedProduct = productRepository.save(product);

            responses.add(new Product.CreateProductResponse(
                    savedProduct.getId(),
                    savedProduct.getName(),
                    savedProduct.getPrice(),
                    savedProduct.getStock()));
        }

        return responses;
    }

    //재고 관리
    @Transactional
    public List<Product.AddStockResponse> addStock(List<Product.AddStockRequest> requests){
        //요청 검증
        validateRequests(requests);
        //재고 수 검증
        validateAddedQuantity(requests);

        //재고 추가
        List<Product.AddStockResponse> responses = new ArrayList<>();

        for(Product.AddStockRequest request : requests) {

            Product targetProduct = productRepository.findById(request.productId())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

            targetProduct.addStock(request.quantity());

            responses.add(new Product.AddStockResponse(
                    targetProduct.getId(), targetProduct.getName(), targetProduct.getPrice(), targetProduct.getStock()
            ));
        }

        return responses;
    }

    //상품 삭제
    @Transactional
    public List<Product.DeleteProductResponse> deleteProduct(List<Product.DeleteProductRequest> requests){

        //요청 검증
        validateRequests(requests);

        //삭제 진행
        List<Product.DeleteProductResponse> responses = new ArrayList<>();

        for(Product.DeleteProductRequest request : requests){
            //검증
            Product targetProduct = productRepository.findById(request.id())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 상품입니다."));

            responses.add(new Product.DeleteProductResponse(
                    targetProduct.getId(),targetProduct.getName(),targetProduct.getPrice(),targetProduct.getStock()
            ));

            productRepository.deleteById(request.id());
        }
        return responses;
    }

    public void validateProduct(List<Product.CreateProductRequest> requests) {
        for (Product.CreateProductRequest request : requests) {
            {
                if (request.name() == null || request.name().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명은 비어 있을 수 없습니다.");
                }
                if (request.price() < 0 || request.stock() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가격과 재고는 0 이상이어야 합니다.");
                }
            }
        }
    }
    public void isDuplicatedProduct(List<Product.CreateProductRequest> requests){
        for (Product.CreateProductRequest request : requests){
            if (productRepository.existsByName(request.name()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복된 이름 입니다.");
        }
    }
    private void validateAddedQuantity(List<Product.AddStockRequest> requests) {
        for (Product.AddStockRequest request : requests){
            if (request.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량은 1 이상이어야 합니다.");
            }
        }
    }
    private void validateOrderedQuantity(Product targetProduct, Product.CreateOrderRequest request) {

        if (request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량은 1 이상이어야 합니다.");
        }
        if (request.quantity() > targetProduct.getStock()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "재고가 부족합니다.");
        }
    }
    private <T> void validateRequests(List<T> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "요청 목록은 비어 있을 수 없습니다."
            );
        }

        for (T request : requests) {
            if (request == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "요청 항목은 null일 수 없습니다."
                );
            }
        }
    }
}
