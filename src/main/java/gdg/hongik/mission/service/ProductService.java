package gdg.hongik.mission.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import gdg.hongik.mission.dto.AddStockRequest;
import gdg.hongik.mission.dto.AddStockResponse;
import gdg.hongik.mission.dto.CreateOrderRequest;
import gdg.hongik.mission.dto.CreateOrderResponse;
import gdg.hongik.mission.dto.CreateProductRequest;
import gdg.hongik.mission.dto.CreateProductResponse;
import gdg.hongik.mission.dto.DeleteProductRequest;
import gdg.hongik.mission.dto.DeleteProductResponse;
import gdg.hongik.mission.dto.GetProductResponse;
import gdg.hongik.mission.dto.OrderedItems;
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

    //전체 상품 조회
    public List<GetProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toGetProductResponse)
                .toList();
    }

    //상품명으로 단건 조회
    public GetProductResponse getProductByName(String name){
        validateProductName(name);

        Product result = productRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "상품을 찾을 수 없습니다."
                ));

        return toGetProductResponse(result);
    }

    private GetProductResponse toGetProductResponse(Product product) {
        return new GetProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock()
        );
    }

    //상품 구매
    @Transactional
    public CreateOrderResponse orderProduct(List<CreateOrderRequest> requests){
        //요청 검증
        validateRequests(requests);

        int totalPrice = 0;
        List<OrderedItems> items = new ArrayList<>();

        for(CreateOrderRequest request : requests){
           
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
            items.add(new OrderedItems(
                    targetProduct.getId(),
                    targetProduct.getName(),
                    request.quantity(),
                    targetPrice,
                    targetProduct.getStock()
            ));
            //가격 계산
            totalPrice += targetPrice;
        }

        return new CreateOrderResponse(items, totalPrice);
    }


    //상품 생성
    @Transactional
    public List<CreateProductResponse> createProduct(List<CreateProductRequest> requests){

        //요청 검증
        validateRequests(requests);

        //재고 수, 이름 공란, 가격 검증
        validateProduct(requests);
        //이름 중복 검증
        isDuplicatedProduct(requests);

        //통과 후 생성
        List<CreateProductResponse> responses = new ArrayList<>();

        for (CreateProductRequest request : requests){

            Product product = new Product(request.name(), request.category(), request.price(), request.stock());
            Product savedProduct = productRepository.save(product);

            responses.add(new CreateProductResponse(
                    savedProduct.getId(),
                    savedProduct.getName(),
                    savedProduct.getCategory(),
                    savedProduct.getPrice(),
                    savedProduct.getStock()));
        }

        return responses;
    }

    //재고 관리
    @Transactional
    public List<AddStockResponse> addStock(List<AddStockRequest> requests){
        //요청 검증
        validateRequests(requests);
        //재고 수 검증
        validateAddedQuantity(requests);

        //재고 추가
        List<AddStockResponse> responses = new ArrayList<>();

        for(AddStockRequest request : requests) {

            Product targetProduct = productRepository.findByName(request.name())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

            targetProduct.addStock(request.quantity());

            responses.add(new AddStockResponse(
                    targetProduct.getId(), targetProduct.getName(), targetProduct.getPrice(), targetProduct.getStock()
            ));
        }

        return responses;
    }

    //상품 삭제
    @Transactional
    public List<DeleteProductResponse> deleteProduct(List<DeleteProductRequest> requests){

        //요청 검증
        validateRequests(requests);

        //삭제 진행
        List<DeleteProductResponse> responses = new ArrayList<>();

        for(DeleteProductRequest request : requests){
            validateProductName(request.name());

            Product targetProduct = productRepository.findByName(request.name())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."));

            responses.add(new DeleteProductResponse(
                    targetProduct.getId(),
                    targetProduct.getName(),
                    targetProduct.getCategory(),
                    targetProduct.getPrice(),
                    targetProduct.getStock()
            ));

            productRepository.delete(targetProduct);
        }
        return responses;
    }

    public void validateProduct(List<CreateProductRequest> requests) {
        for (CreateProductRequest request : requests) {
            {
                if (request.name() == null || request.name().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명은 비어 있을 수 없습니다.");
                }
                if (request.category() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "카테고리는 필수입니다."
                    );
                }
                if (request.price() < 0 || request.stock() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "가격과 재고는 0 이상이어야 합니다.");
                }
            }
        }
    }
    public void isDuplicatedProduct(List<CreateProductRequest> requests){
        for (CreateProductRequest request : requests){
            if (productRepository.existsByName(request.name()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복된 이름 입니다.");
        }
    }
    private void validateAddedQuantity(List<AddStockRequest> requests) {
        for (AddStockRequest request : requests){
            validateProductName(request.name());

            if (request.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량은 1 이상이어야 합니다.");
            }
        }
    }
    private void validateProductName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명은 비어 있을 수 없습니다.");
        }
    }
    private void validateOrderedQuantity(Product targetProduct, CreateOrderRequest request) {

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
