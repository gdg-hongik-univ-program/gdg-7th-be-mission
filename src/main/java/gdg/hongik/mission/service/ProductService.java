package gdg.hongik.mission.service;

import org.springframework.transaction.annotation.Transactional;
import gdg.hongik.mission.global.exception.BadRequestException;
import gdg.hongik.mission.global.exception.NotFoundException;
import gdg.hongik.mission.global.exception.ConflictException;
import gdg.hongik.mission.global.message.ErrorMessage;
import org.springframework.stereotype.Service;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .orElseThrow(() -> new NotFoundException(ErrorMessage.PRODUCT_NOT_FOUND));

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

    //상품 생성
    @Transactional
    public List<CreateProductResponse> createProduct(List<CreateProductRequest> requests){
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
        //재고 추가
        List<AddStockResponse> responses = new ArrayList<>();

        for(AddStockRequest request : requests) {

            Product targetProduct = productRepository.findByName(request.name())
                    .orElseThrow(()-> new NotFoundException(ErrorMessage.PRODUCT_NOT_FOUND));

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
        //삭제 진행
        List<DeleteProductResponse> responses = new ArrayList<>();

        for(DeleteProductRequest request : requests){
            Product targetProduct = productRepository.findByName(request.name())
                    .orElseThrow(()-> new NotFoundException(ErrorMessage.PRODUCT_NOT_FOUND));

            productRepository.delete(targetProduct);
        }

        List<Product> remainingProducts = productRepository.findAll();
        for(Product remainingProduct : remainingProducts){
            responses.add(new DeleteProductResponse(
                    remainingProduct.getName(),
                    remainingProduct.getCategory(),
                    remainingProduct.getPrice(),
                    remainingProduct.getStock()
                    )
            );
        }
        return responses;
    }

    private void isDuplicatedProduct(List<CreateProductRequest> requests){
        Set<String> names = new HashSet<>();

        for (CreateProductRequest request : requests) {
            String name = request.name();

            if (!names.add(name)) {
                throw new BadRequestException(ErrorMessage.DUPLICATE_REQUEST_PRODUCT_NAME + ": " + name);
            }

            if (productRepository.existsByName(name)) {
                throw new BadRequestException(ErrorMessage.PRODUCT_ALREADY_EXISTS + ": " + name);
            }
        }
    }

    private void validateProductName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException(ErrorMessage.PRODUCT_NAME_REQUIRED);
        }
    }
}
